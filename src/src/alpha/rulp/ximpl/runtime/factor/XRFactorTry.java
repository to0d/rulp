/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import static alpha.rulp.lang.Constant.C_ERROR_DEFAULT;
import static alpha.rulp.lang.Constant.C_HANDLE;
import static alpha.rulp.lang.Constant.C_HANDLE_ANY;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorTry extends AbsRFactorAdapter implements IRFactor {

	public XRFactorTry(String factorName) {
		super(factorName);
	}

	public boolean isThreadSafe() {
		return true;
	}

	public void defineHandleCase(IRFrame tryFrame, IRExpr handleExpression) throws RException {

		// (e1 (action1) (action2))

		if (handleExpression.size() < 2) {
			throw new RException("invalid handle expression: " + handleExpression);
		}

		IRAtom errId = RulpUtility.asAtom(handleExpression.get(0));

		if (RulpUtility.isVarAtom(errId)) {

			tryFrame.setEntry(C_HANDLE_ANY, RulpFactory.createList(handleExpression.listIterator(1)));

			// Save default error id
			tryFrame.setEntry(C_ERROR_DEFAULT, errId);

		} else {
			tryFrame.setEntry(C_HANDLE + errId.getName(), RulpFactory.createList(handleExpression.listIterator(1)));
		}

	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject try_exp = args.get(1);
		IRFrame try_frame = RulpFactory.createdChildFrame(frame, "TRY");

		IRIterator<? extends IRObject> iter = args.listIterator(2);
		while (iter.hasNext()) {
			defineHandleCase(try_frame, RulpUtility.asExpression(iter.next()));
		}

		return interpreter.compute(try_frame, try_exp);
//		
//		try {
//			return interpreter.compute(try_frame, try_exp);
//		} catch (RError err) {
//			throw err;
//		}

	}
}
