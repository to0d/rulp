/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorDefun extends AbsRFactorAdapter implements IRFactor {

	public XRFactorDefun(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 4) {
			throw new RException("Invalid parameters: " + args);
		}

		int argIndex = 1;

		/*****************************************************/
		// Function name
		/*****************************************************/
		String funName = RulpUtility.asAtom(args.get(argIndex++)).getName();

		/*****************************************************/
		// Function parameter list
		/*****************************************************/
		List<String> paraNames = new LinkedList<>();
		List<IRAtom> paraTypes = new LinkedList<>();

		{
			IRObject paraObj = args.get(argIndex++);

			if (paraObj.getType() != RType.LIST && paraObj.getType() != RType.EXPR) {
				throw new RException("Invalid para type: " + paraObj);
			}

			IRIterator<? extends IRObject> iter = ((IRList) paraObj).iterator();
			while (iter.hasNext()) {

				IRObject element = iter.next();

				if (element.getType() == RType.ATOM) {
					paraNames.add(RulpUtility.asAtom(element).getName());
					paraTypes.add(null);
					continue;
				}

				if (element.getType() == RType.EXPR) {

					IRList argPair = (IRList) element;
					if (argPair.size() != 2) {
						throw new RException("Invalid para pair: " + argPair);
					}

					IRObject name = argPair.get(0);
					IRObject type = interpreter.compute(frame, argPair.get(1));

					if (name.getType() != RType.ATOM) {
						throw new RException("Invalid para name: " + argPair);
					}

					if (type.getType() == RType.ATOM) {

						paraNames.add(RulpUtility.asAtom(name).getName());
						paraTypes.add((IRAtom) type);

					} else if (type.getType() == RType.CLASS) {

						paraNames.add(RulpUtility.asAtom(name).getName());
						paraTypes.add(((IRClass) type).getClassAtom());

					} else {

						throw new RException("Invalid para type: " + argPair);
					}

					continue;
				}

				throw new RException("Invalid para type: " + paraObj);
			}
		}

		/*****************************************************/
		// Function description
		/*****************************************************/
		String funDescription = null;
		{
			IRObject despObj = args.get(argIndex++);
			if (despObj.getType() != RType.STRING) {
				--argIndex;
			} else {
				funDescription = ((IRString) despObj).asString();
			}
		}

		/*****************************************************/
		// Function body
		/*****************************************************/
		IRList funBody = RulpFactory.createList(args.listIterator(argIndex));

		IRFunction newFun = RulpFactory.createFunction(funName, paraNames, paraTypes, funBody, funDescription);

		/*****************************************************/
		// Function
		/*****************************************************/
		IRFrameEntry entry = frame.getEntry(funName);
		if (entry == null) {
			frame.setEntry(funName, newFun);
			return newFun;
		}

		IRObject entryObj = entry.getObject();
		if (entryObj.getType() != RType.FUNC) {
			throw new RException("Duplicated entry found: " + funName);
		}

		/*****************************************************/
		// Function List (overload)
		/*****************************************************/
		IRFunctionList funList = null;

		// Function List
		if (RulpUtility.asFunction(entryObj).getArgCount() == -1) {
			funList = RulpUtility.asFunctionList(entryObj);
		} else {
			funList = RulpFactory.createFunctionList(funName);
			frame.setEntry(funName, funList);

			funList.addFunc(RulpUtility.asFunction(entryObj));
		}

		funList.addFunc(newFun);
		return newFun;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
