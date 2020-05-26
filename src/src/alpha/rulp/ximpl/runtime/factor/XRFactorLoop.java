/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import static alpha.rulp.lang.Constant.F_DO;
import static alpha.rulp.lang.Constant.F_FOR;
import static alpha.rulp.lang.Constant.F_FROM;
import static alpha.rulp.lang.Constant.F_IN;
import static alpha.rulp.lang.Constant.F_TO;
import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RContinue;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RReturn;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.MathUtility;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorLoop extends AbsRFactorAdapter implements IRFactor {

	public XRFactorLoop(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		if (RulpUtility.isAtom(args.get(1), F_FOR)) {

			if (args.size() >= 7 && RulpUtility.isAtom(args.get(2))) {

				// (loop for x in '(1 2 3) do (print x))
				if (RulpUtility.isAtom(args.get(3), F_IN) && RulpUtility.isAtom(args.get(5), F_DO)) {

					IRFrame loopFrame = RulpFactory.createdChildFrame(frame, "LOOP");
					String indexName = RulpUtility.asAtom(args.get(2)).getName();
					IRList values = RulpUtility.asList(interpreter.compute(loopFrame, args.get(4)));

					IRIterator<? extends IRObject> valIter = values.iterator();
					NEXT_ELE: while (valIter.hasNext()) {
						loopFrame.setEntry(indexName, valIter.next());
						IRIterator<? extends IRObject> argIter = args.listIterator(6);
						while (argIter.hasNext()) {
							try {
								interpreter.compute(loopFrame, argIter.next());
							} catch (RReturn r) {
								return O_Nil;
							} catch (RContinue c) {
								continue NEXT_ELE;
							}
						}
					}

					return O_Nil;
				}

				// (loop for x from 1 to 3 do (print x))
				if (args.size() >= 9 && RulpUtility.isAtom(args.get(3), F_FROM) && RulpUtility.isAtom(args.get(5), F_TO)
						&& RulpUtility.isAtom(args.get(7), F_DO)) {

					IRFrame loopFrame = RulpFactory.createdChildFrame(frame, "LOOP");

					String indexName = RulpUtility.asAtom(args.get(2)).getName();
					int fromIndex = MathUtility
							.toInt(RulpUtility.asInteger(interpreter.compute(loopFrame, args.get(4))));
					int toIndex = MathUtility.toInt(RulpUtility.asInteger(interpreter.compute(loopFrame, args.get(6))));

					NEXT_ELE: for (int i = fromIndex; i <= toIndex; ++i) {
						loopFrame.setEntry(indexName, RulpFactory.createInteger(i));
						IRIterator<? extends IRObject> iter = args.listIterator(6);
						while (iter.hasNext()) {

							try {
								interpreter.compute(loopFrame, iter.next());
							} catch (RReturn r) {
								return O_Nil;
							} catch (RContinue c) {
								continue NEXT_ELE;
							}
						}
					}

					return O_Nil;
				}
			}

			throw new RException("Invalid parameters: " + args);
		}
		// (loop stmt1 stmt2 (return x))
		else {

			// (loop stmt1 stmt2 (return x))

			IRObject rt = null;
			IRIterator<? extends IRObject> iter = null;

			while (rt == null) {

				if (iter == null || !iter.hasNext()) {
					iter = args.listIterator(1); // skip head
				}

				try {
					interpreter.compute(frame, iter.next());

				} catch (RReturn r) {
					rt = r.getReturnValue();
				} catch (RContinue c) {
					rt = null;
				}
			}

			return rt;
		}

	}

}
