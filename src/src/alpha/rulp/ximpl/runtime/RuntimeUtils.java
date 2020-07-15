package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RInterrupt;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRThreadContext;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class RuntimeUtils {

	static class XInterpreterIterator implements IRIterator<IRObject> {

		private IRFrame frame;
		private IRInterpreter interpreter;
		private IRIterator<? extends IRObject> iter;

		public XInterpreterIterator(IRIterator<? extends IRObject> iter, IRInterpreter interpreter, IRFrame frame) {
			super();
			this.iter = iter;
			this.interpreter = interpreter;
			this.frame = frame;
		}

		@Override
		public boolean hasNext() throws RException {
			return iter.hasNext();
		}

		@Override
		public IRObject next() throws RException {
			IRObject obj = iter.next();
			checkAsyncCompleted(obj, frame);
			return interpreter.compute(frame, obj);
		}

	}

	public static IRObject compute(IRObject obj, IRInterpreter interpreter, IRFrame curFrame) throws RException {

		checkAsyncCompleted(obj, curFrame);

		if (obj == null) {
			return O_Nil;
		}

		RType rt = obj.getType();

		switch (rt) {
		case INT:
		case LONG:
		case FLOAT:
		case BOOL:
		case STRING:
		case INSTANCE:
		case NATIVE:
			return obj;

		case VAR: {
			IRVar var = (IRVar) obj;
			IRFrameEntry entry = curFrame.getEntry(var.getName());
			if (entry == null) {
				throw new RException("var entry not found: " + var);
			}

			return RulpUtility.asVar(entry.getObject());
		}

		case ATOM: {
			IRAtom atom = (IRAtom) obj;
			IRFrameEntry entry = curFrame.getEntry(atom.getName());
			IRObject rst = entry == null ? obj : entry.getObject();

			if (rst != null && rst.getType() == RType.VAR) {
				return ((IRVar) rst).getValue();
			}

			return rst;
		}

		case NIL:
			return O_Nil;

		case EXPR:

			IRList expr = (IRList) obj;
			if (expr.size() == 0) {
				return obj;
			}

			IRObject e1 = compute(expr.get(0), interpreter, curFrame);

			switch (e1.getType()) {
			case FACTOR:
			case MACRO:
				return RuntimeUtils.computeCallable((IRCallable) e1, expr, interpreter, curFrame);

			case FUNC:
				return RuntimeUtils.computeFun((IRFunction) e1, expr, interpreter, curFrame);

			default:
				throw new RException("factor not found: " + obj);
			}

		case LIST:

			if (!RuntimeUtils.isComputable(curFrame, obj)) {
				return obj;
			}

			return RulpFactory.createList(new XInterpreterIterator(((IRList) obj).iterator(), interpreter, curFrame));

		default:
			throw new RException("Invalid Type: " + rt + ", obj:" + obj.toString());
		}
	}

	public static IRObject computeCallable(IRCallable callObject, IRList args, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		checkAsyncCompleted(callObject, frame);

		if (callObject.isThreadSafe()) {
			return callObject.compute(args, interpreter, frame);
		} else {
			synchronized (callObject) {
				return callObject.compute(args, interpreter, frame);
			}
		}
	}

	public static IRObject computeFun(IRFunction fun, IRList expr, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		checkAsyncCompleted(fun, frame);

		// For Function List, the argCount is -1
		int argCount = fun.getArgCount();
		if (argCount != -1 && expr.size() != (argCount + 1)) {
			throw new RException(String.format("Unexpect argument number in fun<%s>: expect=%d, actual=%d",
					fun.getName(), argCount, expr.size() - 1));
		}

		if (expr.size() > 1) {

			ArrayList<IRObject> argList = new ArrayList<>();
			argList.add(fun);

			IRIterator<? extends IRObject> argIter = expr.listIterator(1); // Skip factor head element
			while (argIter.hasNext()) {
				argList.add(compute(argIter.next(), interpreter, frame));
			}

			expr = RulpFactory.createExpression(argList);
		}

		return RuntimeUtils.computeCallable(fun, expr, interpreter, frame);
	}

	static void checkAsyncCompleted(IRObject obj, IRFrame frame) throws RInterrupt {

		IRThreadContext atext = frame.getThreadContext();

		if (atext != null && atext.isCompleted()) {
			throw new RInterrupt(obj, frame);
		}
	}

	public static boolean isComputable(IRFrame curFrame, IRObject obj) throws RException {

		if (obj == null) {
			return false;
		}

		switch (obj.getType()) {
		case INT:
		case LONG:
		case FLOAT:
		case BOOL:
		case STRING:
		case NIL:
			return false;

		case ATOM:
			return curFrame.getEntry(((IRAtom) obj).getName()) != null;

		case VAR:
		case EXPR:
			return true;

		case LIST:

			IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
			while (iter.hasNext()) {
				if (isComputable(curFrame, iter.next())) {
					return true;
				}
			}

			return false;

		default:
			return true;
		}
	}

}
