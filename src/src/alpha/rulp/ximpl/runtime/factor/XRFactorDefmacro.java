/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import java.util.List;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorDefmacro extends AbsRFactorAdapter implements IRFactor {

	public XRFactorDefmacro(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		String macroName = null;
		String macroDescription = null;
		List<String> macroParaList = null;

		IRObject nameObj = null;
		IRObject desObj = null;
		IRObject paraObj = null;
		IRObject bodyObj = null;

		if (args.size() == 5) {

			nameObj = args.get(1);
			paraObj = args.get(2);
			desObj = args.get(3);
			bodyObj = args.get(4);

		} else if (args.size() == 4) {

			nameObj = args.get(1);
			paraObj = args.get(2);
			bodyObj = args.get(3);

		} else {
			throw new RException("Invalid parameters: " + args);
		}

		/*****************************************************/
		// Check macro frame
		/*****************************************************/
		IRFrame macroFrame = frame.getParentFrame() == null ? frame : interpreter.getRuntimeFrame();

		/*****************************************************/
		// Check macro name
		/*****************************************************/
		macroName = RulpUtility.asAtom(nameObj).getName();
		if (macroFrame.getEntry(macroName) != null) {
			throw new RException("Duplicated macro name: " + macroName + ", parameters: " + args);
		}

		/*****************************************************/
		// Check macro description
		/*****************************************************/
		if (desObj != null) {
			macroDescription = RulpUtility.asString(desObj).asString();
		}

		/*****************************************************/
		// Check macro parameter list
		/*****************************************************/
		if (paraObj != null) {

			if (!RulpUtility.isPureAtomList(paraObj)) {
				throw new RException("Invalid para type: " + paraObj);

			} else {
				macroParaList = RulpUtility.toStringList(paraObj);
			}
		}

		IRMacro macro = RulpFactory.createMacro(macroName, macroParaList, RulpUtility.asExpression(bodyObj),
				macroDescription);

		macroFrame.setEntry(macroName, macro);

		return macro;
	}

}
