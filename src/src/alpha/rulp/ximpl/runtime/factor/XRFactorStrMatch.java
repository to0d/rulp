/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import java.util.ArrayList;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;
import alpha.rulp.utility.StringUtil;
public class XRFactorStrMatch extends AbsRFactorAdapter implements IRFactor {

	public XRFactorStrMatch(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3 && args.size() != 4) {
			throw new RException("Invalid parameters: " + args);
		}

		String mode = RulpUtility.asString(interpreter.compute(frame, args.get(1))).asString();
		String content = RulpUtility.asString(interpreter.compute(frame, args.get(2))).asString();
		boolean rc;

		if (args.size() == 3) {
			rc = StringUtil.matchFormat(mode, content);

		} else {

			IRList outObjs = RulpUtility.asList(interpreter.compute(frame, args.get(3)));
			if (outObjs.isEmpty()) {
				throw new RException("Invalid parameters: " + args);
			}

			ArrayList<IRVar> outVars = new ArrayList<>();
			for (IRObject var : RulpUtility.toList(outObjs.iterator())) {
				outVars.add(RulpUtility.asVar(var));
			}

			ArrayList<String> values = new ArrayList<>();

			rc = StringUtil.matchFormat(mode, content, values);

			if (values.size() != outVars.size()) {
				return RulpFactory.createBoolean(false);
			}

			for (int i = 0; i < values.size(); ++i) {
				outVars.get(i).setValue(RulpFactory.createString(values.get(i)));
			}
		}

		return RulpFactory.createBoolean(rc);
	}

}