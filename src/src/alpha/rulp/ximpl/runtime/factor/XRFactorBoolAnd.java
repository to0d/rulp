/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import static alpha.rulp.lang.Constant.O_False;
import static alpha.rulp.lang.Constant.O_True;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.RulpUtility;

public class XRFactorBoolAnd extends AbsRFactorAdapter implements IRFactor {

	public XRFactorBoolAnd(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRIterator<? extends IRObject> iter = args.listIterator(1);

		while (iter.hasNext()) {
			if (!RulpUtility.asBoolean(interpreter.compute(frame, iter.next())).asBoolean()) {
				return O_False;
			}
		}

		return O_True;
	}

	public boolean isThreadSafe() {
		return true;
	}
}