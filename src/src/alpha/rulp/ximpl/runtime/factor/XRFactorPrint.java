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
import alpha.rulp.utility.RulpUtility;
import alpha.rulp.utility.StringUtil;

public class XRFactorPrint extends AbsRFactorAdapter implements IRFactor {

	public XRFactorPrint(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject rst = null;
		IRIterator<? extends IRObject> iter = args.listIterator(1);
		while (iter.hasNext()) {
			rst = interpreter.compute(frame, iter.next());
			interpreter.out(StringUtil.removeEscapeString(RulpUtility.toStringPrint(rst)));
		}

		return rst;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
