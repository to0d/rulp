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

public class XRFactorDefvar extends AbsRFactorAdapter implements IRFactor {

	private boolean allowRedefine;

	private boolean rtVar;

	public XRFactorDefvar(String factorName, boolean rtVar, boolean allowRedefine) {
		super(factorName);
		this.rtVar = rtVar;
		this.allowRedefine = allowRedefine;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter intepreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		return defvar(args, intepreter, frame);
	}

	public IRObject defvar(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		String varName = RulpUtility.asAtom(args.get(1)).getName();

		if (!allowRedefine) {
			IRFrameEntry entry = frame.getEntry(varName);
			if (entry != null) {
				return RulpUtility.asVar(entry.getObject());
			}
		}

		IRVar var = RulpFactory.createVar(varName);
		frame.setEntry(varName, var);

		IRObject val = var.getValue();

		if (args.size() == 3) {
			val = interpreter.compute(frame, args.get(2));
			var.setValue(val);
		}

		return rtVar ? var : val;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
