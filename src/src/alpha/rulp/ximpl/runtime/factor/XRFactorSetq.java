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
import alpha.rulp.utility.RulpUtility;

public class XRFactorSetq extends AbsRFactorAdapter implements IRFactor {

	public XRFactorSetq(String factorName) {
		super(factorName);
	}

	public IRVar setVar(IRObject obj, IRObject val, IRInterpreter interpreter, IRFrame frame) throws RException {

		String varName = null;
		switch (obj.getType()) {
		case ATOM:
			varName = RulpUtility.asAtom(obj).getName();
			break;

		case VAR:
			varName = RulpUtility.asVar(obj).getName();
			break;

		case EXPR:
			return setVar(interpreter.compute(frame, obj), val, interpreter, frame);

		default:
			throw new RException("Invalid var: " + obj.toString());
		}

		IRFrameEntry entry = frame.getEntry(varName);
		if (entry == null) {
			throw new RException("var not found: " + varName);
		}

		IRVar var = RulpUtility.asVar(entry.getObject());
		var.setValue(interpreter.compute(frame, val));

		return var;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args.toString());
		}

		return setVar(args.get(1), args.get(2), interpreter, frame);
	}

}
