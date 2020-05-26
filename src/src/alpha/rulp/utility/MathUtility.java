/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utility;

import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;

public class MathUtility {

	static RType calRstType[][] = new RType[RType.TYPE_NUM][RType.TYPE_NUM];

	static {
		calRstType[RType.INT.getIndex()][RType.INT.getIndex()] = RType.INT;
		calRstType[RType.FLOAT.getIndex()][RType.INT.getIndex()] = RType.FLOAT;
		calRstType[RType.INT.getIndex()][RType.FLOAT.getIndex()] = RType.FLOAT;
		calRstType[RType.FLOAT.getIndex()][RType.FLOAT.getIndex()] = RType.FLOAT;

		calRstType[RType.EXPR.getIndex()][RType.EXPR.getIndex()] = RType.EXPR;
		calRstType[RType.ATOM.getIndex()][RType.ATOM.getIndex()] = RType.ATOM;

		calRstType[RType.NIL.getIndex()][RType.NIL.getIndex()] = RType.NIL;
		calRstType[RType.BOOL.getIndex()][RType.BOOL.getIndex()] = RType.BOOL;
	}

	public static RType getConvertType(RType a, RType b) {
		return calRstType[a.getIndex()][b.getIndex()];
	}

	public static boolean toBoolean(IRObject a) throws RException {
		switch (a.getType()) {
		case NIL:
			return false;

		case BOOL:
			return ((IRBoolean) a).asBoolean();

		case FLOAT:
		case INT:
		case EXPR:
			return true;

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static float toFloat(IRObject a) throws RException {
		switch (a.getType()) {
		case FLOAT:
			return ((IRFloat) a).asFloat();
		case INT:
			return ((IRInteger) a).asInteger();
		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static int toInt(IRObject a) throws RException {
		switch (a.getType()) {
		case FLOAT:
			return (int) ((IRFloat) a).asFloat();
		case INT:
			return ((IRInteger) a).asInteger();
		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

}
