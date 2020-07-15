/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import static alpha.rulp.lang.Constant.O_Nan;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpUtility;

public class XRFactorClassOf extends AbsRFactorAdapter implements IRFactor {

	public XRFactorClassOf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		return findClass(args.get(1), frame);
	}

	private IRObject findClass(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {

		case ATOM:
			IRFrameEntry entry = frame.getEntry(RulpUtility.asAtom(obj).getName());
			if (entry != null && entry.getObject() != obj) {
				return findClass(entry.getObject(), frame);
			}

			return O_Nan;

		case INSTANCE:
			return RulpUtility.asInstance(obj).getClassAtom();

		default:
			return O_Nan;
		}
	}

	public boolean isThreadSafe() {
		return true;
	}
}
