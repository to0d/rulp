/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import static alpha.rulp.lang.Constant.O_Nan;

import java.util.ArrayList;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RContinue;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RReturn;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorForeach extends AbsRFactorAdapter implements IRFactor {

	static IRObject getResultObject(IRList args, IRInterpreter interpreter, IRFrame factorFrame) throws RException {

		try {
			IRObject rstObj = null;
			IRIterator<? extends IRObject> iter = args.listIterator(2);
			while (iter.hasNext()) {
				rstObj = interpreter.compute(factorFrame, iter.next());
			}
			return rstObj;
		} catch (RReturn r) {
			return r.getReturnValue();
		} catch (RContinue c) {
			return O_Nan;
		}
	}

	public static boolean isForeachParaList(IRObject obj) throws RException {

		if (obj.getType() != RType.EXPR) {
			return false;
		}

		IRList list = (IRList) obj;

		return list.size() == 2 && RulpUtility.isVarAtom(list.get(0));
	}

	public XRFactorForeach(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		// (foreach (var cond) (action))

		if (args.size() < 3 || !isForeachParaList(args.get(1))) {
			throw new RException("Invalid parameters: " + args);
		}

		IRFrame factorFrame = RulpFactory.createdChildFrame(frame, "FOREACH");
		IRList paraObj = (IRList) args.get(1);

		IRVar var = factorFrame.addVar(RulpUtility.asAtom(paraObj.get(0)).getName());
		IRObject cond = interpreter.compute(factorFrame, paraObj.get(1));

		ArrayList<IRObject> rstList = new ArrayList<>();

		switch (cond.getType()) {
		case LIST:

			IRIterator<? extends IRObject> iter = ((IRList) cond).iterator();
			while (iter.hasNext()) {
				var.setValue(iter.next());
				IRObject rst = getResultObject(args, interpreter, factorFrame);
				if (rst != O_Nan) {
					rstList.add(rst);
				}
			}

			break;

		case ATOM:
			var.setValue(cond);
			IRObject rst = getResultObject(args, interpreter, factorFrame);
			if (rst != O_Nan) {
				rstList.add(rst);
			}

			break;

		default:
			throw new RException("Not support cond type: " + cond);
		}

		return RulpFactory.createList(rstList);
	}

}
