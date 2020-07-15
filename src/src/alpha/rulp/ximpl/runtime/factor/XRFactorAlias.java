/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpUtility;

public class XRFactorAlias extends AbsRFactorAdapter implements IRFactor {

	public XRFactorAlias(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		int argSize = args.size();

		if (argSize < 3) {
			throw new RException("Invalid parameter number: " + args);
		}

		IRFrameEntry srcEntry = null;

		for (int i = 1; i < argSize; ++i) {

			IRAtom arg = RulpUtility.asAtom(args.get(i));
			IRFrameEntry entry = frame.getEntry(arg.getName());

			if (i == 1) {

				if (entry == null) {
					throw new RException("src entry not found: " + arg);
				}

				RType srcType = entry.getObject().getType();

				if (srcType != RType.ATOM && srcType != RType.VAR && srcType != RType.FACTOR) {
					throw new RException("src entry type not support: " + entry);
				}

				srcEntry = entry;

			} else {

				if (entry != null) {
					throw new RException(
							String.format("the name of arg %d is already defined: %s, entry=%s", i, arg, entry));
				}

			}
		}

		for (int i = 2; i < argSize; ++i) {
			frame.setEntryAliasName(srcEntry, ((IRAtom) args.get(i)).getName());
		}

		return srcEntry.getObject();
	}

	public boolean isThreadSafe() {
		return true;
	}

}
