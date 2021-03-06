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

public class XRFactorStrEqualNoCase extends AbsRFactorAdapter implements IRFactor {

	public XRFactorStrEqualNoCase(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String a = interpreter.compute(frame, args.get(1)).asString();
		String b = interpreter.compute(frame, args.get(2)).asString();

		return a.equalsIgnoreCase(b) ? O_True : O_False;
	}

	public boolean isThreadSafe() {
		return true;
	}
}