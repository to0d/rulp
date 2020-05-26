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
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorRef extends AbsRFactorAdapter implements IRFactor {

	public XRFactorRef(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter intepreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		String varName = RulpUtility.asAtom(args.get(1)).getName();
		IRFrameEntry entry = frame.getEntry(varName);
		if (entry != null) {
			return RulpUtility.asVar(entry.getObject());

		}

		IRVar var = RulpFactory.createVar(varName);
		frame.setEntry(varName, var);

		return var;
	}

}
