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
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.MathUtility;
import alpha.rulp.utility.RulpFactory;

public class XRFactorComparison extends AbsRFactorAdapter implements IRFactor {

	public enum ComparisonType {
		Bigger, BiggerOrEqual, Equal, Smaller, SmallerOrEqual
	}

	public static boolean compare(ComparisonType op, IRObject a, IRObject b) throws RException {

		RType at = a.getType();
		RType bt = b.getType();

		RType rt = MathUtility.getConvertType(at, bt);
		if (rt == null) {
			throw new RException(String.format("Invalid op types: %s %s", a.toString(), b.toString()));
		}

		boolean rc;

		switch (rt) {
		case BOOL: {
			boolean av = MathUtility.toBoolean(a);
			boolean bv = MathUtility.toBoolean(b);

			switch (op) {
			case Equal:
				rc = (av == bv);
				break;
			default:
				throw new RException(String.format("Not support op: %s", op));
			}
			break;
		}
		case FLOAT: {

			float av = MathUtility.toFloat(a);
			float bv = MathUtility.toFloat(b);

			switch (op) {
			case Equal:
				rc = (av == bv);
				break;
			case Bigger:
				rc = (av > bv);
				break;
			case BiggerOrEqual:
				rc = (av >= bv);
				break;
			case Smaller:
				rc = (av < bv);
				break;
			case SmallerOrEqual:
				rc = (av <= bv);
				break;
			default:
				throw new RException(String.format("Not support op: %s", op));
			}
			break;
		}

		case INT: {
			int av = MathUtility.toInt(a);
			int bv = MathUtility.toInt(b);

			switch (op) {
			case Equal:
				rc = (av == bv);
				break;
			case Bigger:
				rc = (av > bv);
				break;
			case BiggerOrEqual:
				rc = (av >= bv);
				break;
			case Smaller:
				rc = (av < bv);
				break;
			case SmallerOrEqual:
				rc = (av <= bv);
				break;
			default:
				throw new RException(String.format("Not support op: %s", op));
			}
			break;
		}

		case LONG: {
			long av = MathUtility.toLong(a);
			long bv = MathUtility.toLong(b);

			switch (op) {
			case Equal:
				rc = (av == bv);
				break;
			case Bigger:
				rc = (av > bv);
				break;
			case BiggerOrEqual:
				rc = (av >= bv);
				break;
			case Smaller:
				rc = (av < bv);
				break;
			case SmallerOrEqual:
				rc = (av <= bv);
				break;
			default:
				throw new RException(String.format("Not support op: %s", op));
			}
			break;
		}

		case NIL: {
			if (op != ComparisonType.Equal) {
				throw new RException(String.format("Not support op: %s", op));
			}

			rc = (a.getType() == RType.NIL && b.getType() == RType.NIL);
			break;
		}
		case LIST: {

			if (op != ComparisonType.Equal) {
				throw new RException(String.format("Not support op: %s", op));
			}

			IRList al = (IRList) a;
			IRList bl = (IRList) b;

			if (al.size() != bl.size()) {
				rc = false;

			} else {

				IRIterator<? extends IRObject> ita = al.iterator();
				IRIterator<? extends IRObject> itb = bl.iterator();

				rc = true;

				while (ita.hasNext()) {
					if (!compare(op, ita.next(), itb.next())) {
						rc = false;
					}
				}
			}

			break;
		}
		case ATOM: {

			if (op != ComparisonType.Equal) {
				throw new RException(String.format("Not support op: %s", op));
			}

			rc = ((IRAtom) a).getName().equals(((IRAtom) b).getName());
			break;
		}
		default:
			throw new RException(String.format("Not support type: %s %s", a.toString(), b.toString()));
		}

		return rc;
	}

	private ComparisonType op;

	public XRFactorComparison(String factorName, ComparisonType op) {
		super(factorName);
		this.op = op;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject a = interpreter.compute(frame, args.get(1));
		IRObject b = interpreter.compute(frame, args.get(2));

		return RulpFactory.createBoolean(compare(op, a, b));
	}

	public boolean isThreadSafe() {
		return true;
	}
}
