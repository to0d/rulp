/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.thread.factor;

import java.util.ArrayList;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RInterrupt;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRThreadContext;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;
import alpha.rulp.ximpl.runtime.RuntimeUtils;
import alpha.rulp.ximpl.runtime.factor.AbsRFactorAdapter;

public class XRFactorDoParallel extends AbsRFactorAdapter implements IRFactor {

	static boolean TRACE = false;

	static long beginTime = 0;

	static void trace(String line) {
		long dt = System.currentTimeMillis() - beginTime;
		System.out.println(String.format("%10d: %s", dt, line));
	}

	public static void setTrace(boolean trace) {

		XRFactorDoParallel.TRACE = trace;
		if (trace) {
			XRFactorDoParallel.beginTime = System.currentTimeMillis();
		}
	}

	static void doParallel(IRExpr expr, IRInterpreter interpreter, IRFrame frame, IRThreadContext atext) {

		if (TRACE) {
			trace("doParallel: " + expr);
		}

		IRFrame dopFrame = RulpFactory.createdChildFrame(frame, "DO-P");
		dopFrame.setThreadContext(atext);

		try {
			IRObject rst = RuntimeUtils.compute(expr, interpreter, dopFrame);
			atext.addResult(expr, rst);
			if (TRACE) {
				trace("completed: " + expr);
			}

		} catch (RInterrupt e1) {
			// ignore interrupt result

			if (TRACE) {
				trace("RInterrupt: " + expr);
			}

		} catch (RException e2) {
			atext.addError(expr, e2);

			if (TRACE) {
				trace("RException: " + expr);
			}

		}
	}

	public XRFactorDoParallel(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		if (args.size() == 2) {
			return interpreter.compute(frame, RulpUtility.asExpression(args.get(1)));
		}

		ArrayList<IRExpr> parallelExprs = new ArrayList<>();
		IRIterator<? extends IRObject> iter = args.listIterator(1);
		while (iter.hasNext()) {
			parallelExprs.add(RulpUtility.asExpression(iter.next()));
		}

		IRThreadContext newText = RulpFactory.createThreadContext();

		for (IRExpr pexpr : parallelExprs) {

			new Thread(new Runnable() {
				@Override
				public void run() {

					doParallel(pexpr, interpreter, frame, newText);

					synchronized (newText) {
						
						newText.notify();
						if (TRACE) {
							trace("notify" + pexpr);
						}
					}

				}
			}).start();
		}

		// wait notify at least one
		do {
			synchronized (newText) {
				try {
					newText.wait(50);
					if (TRACE) {
						trace("wait done");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} while (!newText.isCompleted());

		if (!newText.isCompleted()) {
			if (newText.getErrorCount() == 0) {
				throw new RException("dop not completed, no error found: " + args);
			}

			throw newText.getException(0);
		}

		if (newText.getResultCount() == 0) {
			throw new RException("dop completed but no result found: " + args);
		}

		return newText.getResult(0);
	}

	public boolean isThreadSafe() {
		return true;
	}
}
