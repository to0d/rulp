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
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorStrCat extends AbsRFactorAdapter implements IRFactor {

	public XRFactorStrCat(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
		
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < args.size(); ++i) {
			sb.append(RulpUtility.asString(interpreter.compute(frame, args.get(i))).asString());
		}

		return RulpFactory.createString(sb.toString());
	}

}