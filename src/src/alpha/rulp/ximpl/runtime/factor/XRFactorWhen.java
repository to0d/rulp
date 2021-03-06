/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.MathUtility;

public class XRFactorWhen extends AbsRFactorAdapter implements IRFactor {

	public XRFactorWhen(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject cond = args.get(1);
		IRObject ifClause = interpreter.compute(frame, cond);

		// false
		if (!MathUtility.toBoolean(ifClause)) {
			return O_Nil;
		}

		IRObject rstClause = null;
		IRIterator<? extends IRObject> iter = args.listIterator(2);
		while (iter.hasNext()) {
			rstClause = interpreter.compute(frame, iter.next());
		}

		return rstClause;
	}

	public boolean isThreadSafe() {
		return true;
	}

}
