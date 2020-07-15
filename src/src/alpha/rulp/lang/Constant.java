/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import alpha.rulp.utility.RulpFactory;

public interface Constant {

	String A_ATOM = "ATOM";

	String A_BOOL = "BOOL";

	String A_CLASS = "CLASS";

	String A_COLUMN = "column";

	String A_CORE = "core";

	String A_ERROR = "error";

	String A_EXPRESSION = "EXPRESSION";

	String A_FACTOR = "FACTOR";

	String A_FALSE = "false";

	String A_FLOAT = "FLOAT";

	String A_FUNCTION = "FUNCTION";

	String A_INSTANCE = "INSTANCE";

	String A_INTEGER = "INTEGER";

	String A_INTO = "into";

	String A_LIST = "LIST";

	String A_LONG = "LONG";

	String A_MACRO = "MACRO";

	String A_NAN = "nan";

	String A_NATIVE = "NATIVE";

	String A_NIL = "nil";

	String A_NULL = "NULL";

	String A_STRING = "STRING";

	String A_TRUE = "true";

	String A_VALUES = "values";

	String A_VAR = "VAR";

	IRAtom C_COLUMN = RulpFactory.createAtom(A_COLUMN);

	IRAtom C_ERROR = RulpFactory.createAtom(A_ERROR);

	String C_ERROR_DEFAULT = "_$error$_";

	String C_FUN_ARG_SEP = "_$arg$_";

	String C_HANDLE = "_$handle$_";

	String C_HANDLE_ANY = "_$handle$any$_";

	String F_ALIAS = "alias";

	String F_B_AND = "and";

	String F_B_NOT = "not";

	String F_B_OR = "or";

	String F_CLASS_OF = "class-of";

	String F_CONTINUE = "continue";

	String F_CREATE = "create";

	String F_DATE = "date";

	String F_DEFMACRO = "defmacro";

	String F_DEFUN = "defun";

	String F_DEFVAR = "defvar";

	String F_DELETE = "delete";

	String F_DO = "do";

	String F_DO_Parallel= "do-p";

	String F_E_ERROR = "error";

	String F_E_GET_ERR_VALUE = "get-err-value";

	String F_E_TRY = "try";

	String F_EQUAL = "equal";

	String F_FOR = "for";

	String F_FOREACH = "foreach";

	String F_FROM = "from";

	String F_GET = "get";

	String F_IF = "if";

	String F_IN = "in";

	String F_JOIN = "join";

	String F_LAMBDA = "lambda";

	String F_LET = "let";

	String F_LOAD = "load";

	String F_LOOP = "loop";

	String F_LS = "ls";

	String F_NAME_OF = "name-of";

	String F_NOT_EQUAL = "not-equal";

	String F_O_ADD = "+";

	String F_O_BY = "*";

	String F_O_DIV = "/";

	String F_O_EQ = "=";

	String F_O_GE = ">=";

	String F_O_GT = ">";

	String F_O_LE = "<=";

	String F_O_LT = "<";

	String F_O_REF = "&";

	String F_O_SUB = "-";

	String F_OUT_TO_FILE = "out-to-file";

	String F_PRINT = "print";

	String F_REF = "ref";

	String F_RETURN = "return";

	String F_SETQ = "setq";

	String F_SIZE_OF_LIST = "size-of-list";

	String F_STR_EQUAL = "str-equal";

	String F_STR_EQUAL_NOCASE = "str-equal-nocase";

	String F_STR_MATCH = "str-match";

	String F_STR_TRIM = "str-trim";

	String F_STR_TRIM_HEAD = "str-trim-head";

	String F_STR_TRIM_TAIL = "str-trim-tail";

	String F_STRCAT = "strcat";

	String F_SYS_GC = "sys-gc";

	String F_SYS_TIME = "sys-time";
	
	String F_T_SLEEP = "sleep";

	String F_TO = "to";

	String F_TO_ATOM = "to-atom";

	String F_TO_STRING = "to-string";

	String F_TYPE_OF = "type-of";

	String F_UNION = "union";

	String F_WATCH_VAR = "watch-var";

	String F_WHEN = "when";

	int MAX_TOSTRING_LEN = 256;

	IRBoolean O_False = RulpFactory.createBoolean(false);

	IRAtom O_Nan = RulpFactory.createAtom(A_NAN);

	IRAtom O_Nil = RulpFactory.createNil();

	IRBoolean O_True = RulpFactory.createBoolean(true);

	IRAtom T_Atom = RulpFactory.createAtom(A_ATOM);

	IRAtom T_Bool = RulpFactory.createAtom(A_BOOL);

	IRAtom T_Class = RulpFactory.createAtom(A_CLASS);

	IRAtom T_Expr = RulpFactory.createAtom(A_EXPRESSION);

	IRAtom T_Factor = RulpFactory.createAtom(A_FACTOR);

	IRAtom T_Float = RulpFactory.createAtom(A_FLOAT);

	IRAtom T_Func = RulpFactory.createAtom(A_FUNCTION);

	IRAtom T_Instance = RulpFactory.createAtom(A_INSTANCE);

	IRAtom T_Int = RulpFactory.createAtom(A_INTEGER);

	IRAtom T_List = RulpFactory.createAtom(A_LIST);

	IRAtom T_Long = RulpFactory.createAtom(A_LONG);

	IRAtom T_Macro = RulpFactory.createAtom(A_MACRO);

	IRAtom T_Native = RulpFactory.createAtom(A_NATIVE);

	IRAtom T_Null = RulpFactory.createAtom(A_NULL);

	IRAtom T_String = RulpFactory.createAtom(A_STRING);

	IRAtom T_Var = RulpFactory.createAtom(A_VAR);

}
