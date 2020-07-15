/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import static alpha.rulp.lang.Constant.A_NAN;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorNameOf extends AbsRFactorAdapter implements IRFactor {

	public XRFactorNameOf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		return getName(args.get(1), frame);
	}

	private IRObject getName(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {

		case ATOM:
			IRFrameEntry entry = frame.getEntry(RulpUtility.asAtom(obj).getName());
			if (entry != null && entry.getObject() != obj) {
				return getName(entry.getObject(), frame);
			}

			return RulpFactory.createString(RulpUtility.asAtom(obj).getName());

		case INSTANCE:
			return RulpFactory.createString(RulpUtility.asInstance(obj).getInstanceName());

		case CLASS:
			return RulpFactory.createString(RulpUtility.asClass(obj).getClassAtom().getName());

		case FACTOR:
			return RulpFactory.createString(RulpUtility.asFactor(obj).getName());

		case FUNC:
			return RulpFactory.createString(RulpUtility.asFunction(obj).getSignature());

		case MACRO:
			return RulpFactory.createString(RulpUtility.asMacro(obj).getName());

		case NIL:
			return obj;

		case VAR:
			return RulpFactory.createString(RulpUtility.asVar(obj).getName());

		default:
			return RulpFactory.createString(A_NAN);

		}

	}

	public boolean isThreadSafe() {
		return true;
	}
}
