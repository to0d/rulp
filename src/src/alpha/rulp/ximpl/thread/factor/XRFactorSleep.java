/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.thread.factor;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpUtility;
import alpha.rulp.ximpl.runtime.factor.AbsRFactorAdapter;

public class XRFactorSleep extends AbsRFactorAdapter implements IRFactor {

	public XRFactorSleep(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		int millis = RulpUtility.asInteger(interpreter.compute(frame, args.get(1))).asInteger();

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RException(e.toString());
		}

		return O_Nil;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
