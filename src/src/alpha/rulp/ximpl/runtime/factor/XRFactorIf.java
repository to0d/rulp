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
import alpha.rulp.utility.MathUtility;

public class XRFactorIf extends AbsRFactorAdapter implements IRFactor {

	public XRFactorIf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3 && args.size() != 4) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject cond = args.get(1);
		IRObject ifClause = interpreter.compute(frame, cond);
		IRObject rstClause = null;

		if (MathUtility.toBoolean(ifClause)) {
			rstClause = args.get(2); // trueClause
		} else {
			rstClause = args.size() > 3 ? args.get(3) : null; // falseClause
		}

		return interpreter.compute(frame, rstClause);
	}

}
