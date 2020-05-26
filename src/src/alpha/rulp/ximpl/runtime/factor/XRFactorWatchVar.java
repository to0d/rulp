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
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.runtime.IRVarListener;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorWatchVar extends AbsRFactorAdapter implements IRFactor {

	public XRFactorWatchVar(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String varName = RulpUtility.asAtom(args.get(1)).getName();

		IRFrameEntry entry = frame.getEntry(varName);
		if (entry == null) {
			throw new RException("var not found: " + varName);
		}

		IRVar var = RulpUtility.asVar(entry.getObject());

		IRFunction fun = RulpUtility.asFunction(interpreter.compute(frame, args.get(2)));

		if (fun.getArgCount() != 3) {
			throw new RException("the watch func need 2 arguments");
		}

		var.addListener(new IRVarListener() {

			@Override
			public void valueChanged(IRVar var, IRObject oldVal, IRObject newVal) throws RException {

				IRList newArgs = RulpFactory.createList(fun, var, oldVal, newVal);
				fun.compute(newArgs, interpreter, frame);
			}
		});

		return fun;
	}

}