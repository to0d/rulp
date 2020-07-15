/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import static alpha.rulp.lang.Constant.A_ATOM;
import static alpha.rulp.lang.Constant.A_BOOL;
import static alpha.rulp.lang.Constant.A_CLASS;
import static alpha.rulp.lang.Constant.A_EXPRESSION;
import static alpha.rulp.lang.Constant.A_FACTOR;
import static alpha.rulp.lang.Constant.A_FLOAT;
import static alpha.rulp.lang.Constant.A_FUNCTION;
import static alpha.rulp.lang.Constant.A_INSTANCE;
import static alpha.rulp.lang.Constant.A_INTEGER;
import static alpha.rulp.lang.Constant.A_LIST;
import static alpha.rulp.lang.Constant.A_LONG;
import static alpha.rulp.lang.Constant.A_MACRO;
import static alpha.rulp.lang.Constant.A_NATIVE;
import static alpha.rulp.lang.Constant.A_NULL;
import static alpha.rulp.lang.Constant.A_STRING;
import static alpha.rulp.lang.Constant.A_VAR;
import static alpha.rulp.lang.Constant.O_Nan;
import static alpha.rulp.lang.Constant.T_Atom;
import static alpha.rulp.lang.Constant.T_Bool;
import static alpha.rulp.lang.Constant.T_Class;
import static alpha.rulp.lang.Constant.T_Expr;
import static alpha.rulp.lang.Constant.T_Factor;
import static alpha.rulp.lang.Constant.T_Float;
import static alpha.rulp.lang.Constant.T_Func;
import static alpha.rulp.lang.Constant.T_Instance;
import static alpha.rulp.lang.Constant.T_Int;
import static alpha.rulp.lang.Constant.T_List;
import static alpha.rulp.lang.Constant.T_Long;
import static alpha.rulp.lang.Constant.T_Macro;
import static alpha.rulp.lang.Constant.T_Native;
import static alpha.rulp.lang.Constant.T_Null;
import static alpha.rulp.lang.Constant.T_String;
import static alpha.rulp.lang.Constant.T_Var;

public enum RType {

	ATOM(1, A_ATOM), //
	BOOL(2, A_BOOL), //
	CLASS(14, A_CLASS), //
	EXPR(11, A_EXPRESSION), //
	FACTOR(8, A_FACTOR), //
	FLOAT(5, A_FLOAT), //
	FUNC(9, A_FUNCTION), //
	INSTANCE(13, A_INSTANCE), //
	INT(3, A_INTEGER), //
	LIST(12, A_LIST), //
	LONG(4, A_LONG), //
	MACRO(10, A_MACRO), //
	NATIVE(15, A_NATIVE), //
	NIL(0, A_NULL), //
	STRING(6, A_STRING), //
	VAR(7, A_VAR);

	public static final int TYPE_NUM = 16;

	public static IRAtom toObject(RType type) {

		switch (type) {
		case ATOM:
			return T_Atom;

		case BOOL:
			return T_Bool;

		case INSTANCE:
			return T_Instance;

		case EXPR:
			return T_Expr;

		case FACTOR:
			return T_Factor;

		case FLOAT:
			return T_Float;

		case FUNC:
			return T_Func;

		case INT:
			return T_Int;
			
		case LONG:
			return T_Long;

		case LIST:
			return T_List;

		case MACRO:
			return T_Macro;

		case NATIVE:
			return T_Native;

		case NIL:
			return T_Null;

		case STRING:
			return T_String;

		case VAR:
			return T_Var;

		case CLASS:
			return T_Class;

		default:
			return O_Nan;
		}
	}

	public static RType toType(String name) throws RException {

		switch (name) {
		case A_ATOM:
			return ATOM;
		case A_BOOL:
			return BOOL;
		case A_INSTANCE:
			return INSTANCE;
		case A_EXPRESSION:
			return EXPR;
		case A_FACTOR:
			return FACTOR;
		case A_FLOAT:
			return FLOAT;
		case A_FUNCTION:
			return FUNC;
		case A_INTEGER:
			return INT;
		case A_LONG:
			return LONG;
		case A_LIST:
			return LIST;
		case A_MACRO:
			return MACRO;
		case A_NATIVE:
			return NATIVE;
		case A_NULL:
			return NIL;
		case A_STRING:
			return STRING;
		case A_VAR:
			return VAR;
		case A_CLASS:
			return CLASS;
		default:
			throw new RException("unknow type");
		}
	}

	private int index;

	private String name;

	private RType(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}
}
