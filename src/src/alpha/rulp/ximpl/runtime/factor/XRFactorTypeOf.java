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
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpUtility;

public class XRFactorTypeOf extends AbsRFactorAdapter implements IRFactor {

	public XRFactorTypeOf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = args.get(1);
		RType type = obj.getType();

		switch (obj.getType()) {
		case ATOM:
			IRFrameEntry entry = frame.getEntry(RulpUtility.asAtom(obj).getName());
			if (entry != null) {
				type = entry.getObject().getType();
			}

			break;

//		case CLASS:
//			return RulpFactory.createString(RulpUtility.asClass(obj).getClassName());

		default:
		}

		return RType.toObject(type);
	}

	public boolean isThreadSafe() {
		return true;
	}
}
