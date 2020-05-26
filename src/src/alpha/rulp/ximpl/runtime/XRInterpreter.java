/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RIException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IROut;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRInterpreter implements IRInterpreter {

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
			return interpreter.compute(frame, iter.next());
		}

	}

	static List<IRObject> EMPTY_OBJ_LIST = new LinkedList<>();

	public static boolean TRACE = false;

	static {
		EMPTY_OBJ_LIST = Collections.unmodifiableList(EMPTY_OBJ_LIST);
	}

	private IROut out;

	private IRParser parser;

	private IRFrame runtimeFrame;

	private IRFrame sysFrame;

	public XRInterpreter() throws RException {
		super();
	}

	private IRObject _computeFun(IRFrame curFrame, IRFunction fun, IRList expr) throws RException {

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
				argList.add(this.compute(curFrame, argIter.next()));
			}

			expr = RulpFactory.createExpression(argList);
		}

		return fun.compute(expr, this, curFrame);
	}

	private boolean _isComputable(IRFrame curFrame, IRObject obj) throws RException {

		if (obj == null) {
			return false;
		}

		switch (obj.getType()) {
		case INT:
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
				if (_isComputable(curFrame, iter.next())) {
					return true;
				}
			}

			return false;

		default:
			return true;
		}
	}

	@Override
	public void addObject(IRObject obj) throws RException {

		switch (obj.getType()) {
		case NIL:
		case ATOM:
			IRAtom atom = (IRAtom) obj;
			runtimeFrame.setEntry(atom.getName(), atom);
			break;

		case BOOL:
			IRBoolean bv = (IRBoolean) obj;
			runtimeFrame.setEntry(bv.asString(), bv);
			break;

		case FACTOR:
			IRFactor factor = (IRFactor) obj;
			runtimeFrame.setEntry(factor.getName(), factor);
			break;

		case INSTANCE:
			IRInstance cv = (IRInstance) obj;
			runtimeFrame.setEntry(cv.getInstanceName(), cv);
			break;

		default:
			throw new RException("Invalid object: " + obj);
		}

	}

	@Override
	public IRObject compute(IRFrame curFrame, IRObject obj) throws RException {

		if (obj == null) {
			return O_Nil;
		}

		RType rt = obj.getType();

		switch (rt) {
		case INT:
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

			if (rst.getType() == RType.VAR) {
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

			IRObject e1 = compute(curFrame, expr.get(0));
			switch (e1.getType()) {
			case FACTOR:
			case MACRO:
				return ((IRCallable) e1).compute(expr, this, curFrame);

			case FUNC:
				return _computeFun(curFrame, (IRFunction) e1, expr);

			default:
				throw new RException("factor not found: " + obj);
			}

		case LIST:
			if (!_isComputable(curFrame, obj)) {
				return obj;
			}

			return RulpFactory.createList(new XInterpreterIterator(((IRList) obj).iterator(), this, curFrame));

		default:
			throw new RException("Invalid Type: " + rt + ", obj:" + obj.toString());

		}
	}

	@Override
	public List<IRObject> compute(String input) throws RException {

		try {

			List<IRObject> rsts = new LinkedList<>();
			for (IRObject obj : parser.parse(input)) {
				rsts.add(compute(runtimeFrame, obj));
			}
			return rsts;

		} catch (RIException e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException("Unhandled internal exception: " + e.toString());

		} catch (RError e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException("Unhandled error: id=\"" + e.getError() + "\", from=\"" + e.getFromObject() + "\"");
		}
	}

	@Override
	public IRObject getObject(String name) throws RException {
		IRFrameEntry entry = runtimeFrame.getEntry(name);
		return entry == null ? null : entry.getObject();
	}

	@Override
	public IROut getOut() {
		return out;
	}

	@Override
	public IRParser getParser() {
		return parser;
	}

	@Override
	public IRFrame getRuntimeFrame() {
		return runtimeFrame;
	}

	public IRFrame getSystemFrame() {
		return sysFrame;
	}

	@Override
	public void out(String line) {

		if (out != null) {
			out.out(line);
		} else {
			System.out.print(line);
		}
	}

	@Override
	public void setOutput(IROut out) {
		this.out = out;
	}

	public void setParser(IRParser parser) {
		this.parser = parser;
	}

	public void setSystemFrame(IRFrame sysFrame) {
		this.sysFrame = sysFrame;
		this.runtimeFrame = RulpFactory.createdChildFrame(sysFrame, "RUN");
		;
	}

}
