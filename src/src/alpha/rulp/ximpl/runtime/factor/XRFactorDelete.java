/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpUtility;

public class XRFactorDelete extends AbsRFactorAdapter implements IRFactor {

	public XRFactorDelete(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		String entryName = RulpUtility.asAtom(args.get(1)).getName();

		IRFrameEntry oldEntry = frame.removeEntry(entryName);
		if (oldEntry == null) {
			throw new RException(String.format("unable to delete: obj %s not found", entryName));
		}

		IRObject obj = oldEntry.getObject();
		if (obj.getType() == RType.INSTANCE) {

			IRInstance instance = RulpUtility.asInstance(obj);
			IRClass rclass = RulpUtility.findClass(obj, frame);
			rclass.destroyInstance(instance, interpreter, frame);
		}

		return obj;
	}
}
