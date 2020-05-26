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
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorCreate extends AbsRFactorAdapter implements IRFactor {

	public XRFactorCreate(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRClass rClass = RulpUtility.asClass(interpreter.compute(frame, args.get(1)));
		String instanceName = RulpUtility.asAtom(interpreter.compute(frame, args.get(2))).getName();

		/******************************************/
		// Check instance exist
		/******************************************/
		{
			IRFrameEntry oldEntry = frame.getEntry(instanceName);
			if (oldEntry != null) {
				throw new RException(String.format("duplicate name<%s> found, unable to create instance: %s",
						instanceName, oldEntry.getObject()));
			}
		}

		IRInstance instance = rClass.createInstance(instanceName, RulpFactory.createList(args.listIterator(3)),
				interpreter, frame);

		if (instance == null) {
			throw new RException(this, "Nullpointer from Class Builder: " + rClass);
		}

		/******************************************/
		// Add into frame
		/******************************************/
		frame.setEntry(instanceName, instance);

		return instance;
	}
}
