/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import static alpha.rulp.lang.Constant.C_ERROR_DEFAULT;
import static alpha.rulp.lang.Constant.C_HANDLE;
import static alpha.rulp.lang.Constant.C_HANDLE_ANY;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRError;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RReturn;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorError extends AbsRFactorAdapter implements IRFactor {

	public XRFactorError(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRAtom errId = RulpUtility.asAtom(interpreter.compute(frame, args.get(1)));
		IRObject errValue = null;
		if (args.size() == 3) {
			errValue = interpreter.compute(frame, args.get(2));
		}

		IRError err = RulpFactory.createError(errId, errValue);

		String handleName = C_HANDLE + errId.getName();
		String valueName = errId.getName();

		IRFrameEntry handlEntry = frame.getEntry(handleName);
		if (handlEntry == null) {

			handlEntry = frame.getEntry(C_HANDLE_ANY);
			if (handlEntry == null) {
				throw new RError(frame, this, err);
			}

			valueName = RulpUtility.asAtom(frame.getEntry(C_ERROR_DEFAULT).getObject()).getName();
		}

		IRList actionList = RulpUtility.asList(handlEntry.getObject());
		frame.setEntry(valueName, err);

		try {

			IRIterator<? extends IRObject> iter = actionList.iterator();
			IRObject rst = null;
			while (iter.hasNext()) {
				rst = interpreter.compute(frame, iter.next());
			}
			return rst;

		} catch (RReturn r) {
			return r.getReturnValue();
		}
	}

}
