/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorLet extends AbsRFactorAdapter implements IRFactor {

	public XRFactorLet(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList varList = RulpUtility.asExpression(args.get(1));
		if (varList.size() < 2 || (varList.size() % 2) != 0) {
			throw new RException("Invalid parameters: " + args);
		}

		IRFrame letFrame = RulpFactory.createdChildFrame(frame, "LET");

		IRIterator<? extends IRObject> it = varList.iterator();
		while (it.hasNext()) {
			String varName = (RulpUtility.asAtom(it.next())).getName();
			IRVar var = RulpFactory.createVar(varName);
			letFrame.setEntry(varName, var);
			IRObject val = interpreter.compute(letFrame, it.next());
			var.setValue(val);
		}

		return interpreter.compute(letFrame, args.get(2));
	}

	public boolean isThreadSafe() {
		return true;
	}
}
