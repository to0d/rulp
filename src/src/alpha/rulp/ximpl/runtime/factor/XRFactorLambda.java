/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import java.util.List;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorLambda extends AbsRFactorAdapter implements IRFactor {

	public XRFactorLambda(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		List<String> paraNames = null;

		/*****************************************************/
		// Check function parameter list
		/*****************************************************/
		IRObject paraObj = args.get(1);
		if (!RulpUtility.isPureAtomList(paraObj)) {
			throw new RException("Invalid para type: " + paraObj);
		} else {
			paraNames = RulpUtility.toStringList(paraObj);
		}

		return RulpFactory.createFunctionLambda(paraNames, null, RulpFactory.createList(args.listIterator(2)),
				RulpFactory.createdChildFrame(frame, "LAMBDA"));
	}
}
