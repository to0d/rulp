/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.F_ALIAS;
import static alpha.rulp.lang.Constant.F_B_AND;
import static alpha.rulp.lang.Constant.F_B_NOT;
import static alpha.rulp.lang.Constant.*;
import static alpha.rulp.lang.Constant.F_CLASS_OF;
import static alpha.rulp.lang.Constant.F_CONTINUE;
import static alpha.rulp.lang.Constant.F_CREATE;
import static alpha.rulp.lang.Constant.F_DATE;
import static alpha.rulp.lang.Constant.F_DEFMACRO;
import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.F_DEFVAR;
import static alpha.rulp.lang.Constant.F_DELETE;
import static alpha.rulp.lang.Constant.F_DO;
import static alpha.rulp.lang.Constant.F_DO_Parallel;
import static alpha.rulp.lang.Constant.F_EQUAL;
import static alpha.rulp.lang.Constant.F_E_ERROR;
import static alpha.rulp.lang.Constant.F_E_GET_ERR_VALUE;
import static alpha.rulp.lang.Constant.F_E_TRY;
import static alpha.rulp.lang.Constant.F_FOREACH;
import static alpha.rulp.lang.Constant.F_GET;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_JOIN;
import static alpha.rulp.lang.Constant.F_LAMBDA;
import static alpha.rulp.lang.Constant.F_LET;
import static alpha.rulp.lang.Constant.F_LOAD;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_NAME_OF;
import static alpha.rulp.lang.Constant.F_NOT_EQUAL;
import static alpha.rulp.lang.Constant.F_OUT_TO_FILE;
import static alpha.rulp.lang.Constant.F_O_ADD;
import static alpha.rulp.lang.Constant.F_O_BY;
import static alpha.rulp.lang.Constant.F_O_DIV;
import static alpha.rulp.lang.Constant.F_O_EQ;
import static alpha.rulp.lang.Constant.F_O_GE;
import static alpha.rulp.lang.Constant.F_O_GT;
import static alpha.rulp.lang.Constant.F_O_LE;
import static alpha.rulp.lang.Constant.F_O_LT;
import static alpha.rulp.lang.Constant.F_O_SUB;
import static alpha.rulp.lang.Constant.F_PRINT;
import static alpha.rulp.lang.Constant.F_REF;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.F_SETQ;
import static alpha.rulp.lang.Constant.F_SIZE_OF_LIST;
import static alpha.rulp.lang.Constant.F_STRCAT;
import static alpha.rulp.lang.Constant.F_STR_EQUAL;
import static alpha.rulp.lang.Constant.F_STR_EQUAL_NOCASE;
import static alpha.rulp.lang.Constant.F_STR_MATCH;
import static alpha.rulp.lang.Constant.F_STR_TRIM;
import static alpha.rulp.lang.Constant.F_STR_TRIM_HEAD;
import static alpha.rulp.lang.Constant.F_STR_TRIM_TAIL;
import static alpha.rulp.lang.Constant.F_SYS_GC;
import static alpha.rulp.lang.Constant.F_SYS_TIME;
import static alpha.rulp.lang.Constant.F_TO_ATOM;
import static alpha.rulp.lang.Constant.F_TO_STRING;
import static alpha.rulp.lang.Constant.F_TYPE_OF;
import static alpha.rulp.lang.Constant.F_T_SLEEP;
import static alpha.rulp.lang.Constant.F_UNION;
import static alpha.rulp.lang.Constant.F_VALUE_OF;
import static alpha.rulp.lang.Constant.F_VALUE_TYPE_OF;
import static alpha.rulp.lang.Constant.F_WATCH_VAR;
import static alpha.rulp.lang.Constant.F_WHEN;
import static alpha.rulp.lang.Constant.O_False;
import static alpha.rulp.lang.Constant.O_Nan;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.O_True;
import static alpha.rulp.lang.Constant.T_Atom;
import static alpha.rulp.lang.Constant.T_Bool;
import static alpha.rulp.lang.Constant.T_Expr;
import static alpha.rulp.lang.Constant.T_Factor;
import static alpha.rulp.lang.Constant.T_Float;
import static alpha.rulp.lang.Constant.T_Func;
import static alpha.rulp.lang.Constant.T_Instance;
import static alpha.rulp.lang.Constant.T_Int;
import static alpha.rulp.lang.Constant.T_List;
import static alpha.rulp.lang.Constant.T_Macro;
import static alpha.rulp.lang.Constant.T_Native;
import static alpha.rulp.lang.Constant.T_Null;
import static alpha.rulp.lang.Constant.T_String;
import static alpha.rulp.lang.Constant.T_Var;

import java.io.IOException;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.utility.RulpUtility;
import alpha.rulp.ximpl.runtime.factor.XRFactorAlias;
import alpha.rulp.ximpl.runtime.factor.XRFactorArithmetic;
import alpha.rulp.ximpl.runtime.factor.XRFactorArithmetic.ArithmeticType;
import alpha.rulp.ximpl.runtime.factor.XRFactorBoolAnd;
import alpha.rulp.ximpl.runtime.factor.XRFactorBoolNot;
import alpha.rulp.ximpl.runtime.factor.XRFactorBoolOr;
import alpha.rulp.ximpl.runtime.factor.XRFactorClassOf;
import alpha.rulp.ximpl.runtime.factor.XRFactorColJoin;
import alpha.rulp.ximpl.runtime.factor.XRFactorColUnion;
import alpha.rulp.ximpl.runtime.factor.XRFactorComparison;
import alpha.rulp.ximpl.runtime.factor.XRFactorComparison.ComparisonType;
import alpha.rulp.ximpl.runtime.factor.XRFactorContinue;
import alpha.rulp.ximpl.runtime.factor.XRFactorCreate;
import alpha.rulp.ximpl.runtime.factor.XRFactorDate;
import alpha.rulp.ximpl.runtime.factor.XRFactorDefmacro;
import alpha.rulp.ximpl.runtime.factor.XRFactorDefun;
import alpha.rulp.ximpl.runtime.factor.XRFactorDefvar;
import alpha.rulp.ximpl.runtime.factor.XRFactorDelete;
import alpha.rulp.ximpl.runtime.factor.XRFactorDo;
import alpha.rulp.ximpl.runtime.factor.XRFactorEqual;
import alpha.rulp.ximpl.runtime.factor.XRFactorError;
import alpha.rulp.ximpl.runtime.factor.XRFactorForeach;
import alpha.rulp.ximpl.runtime.factor.XRFactorGet;
import alpha.rulp.ximpl.runtime.factor.XRFactorGetErrValue;
import alpha.rulp.ximpl.runtime.factor.XRFactorIf;
import alpha.rulp.ximpl.runtime.factor.XRFactorLambda;
import alpha.rulp.ximpl.runtime.factor.XRFactorLet;
import alpha.rulp.ximpl.runtime.factor.XRFactorLoad;
import alpha.rulp.ximpl.runtime.factor.XRFactorLoop;
import alpha.rulp.ximpl.runtime.factor.XRFactorNameOf;
import alpha.rulp.ximpl.runtime.factor.XRFactorNotEqual;
import alpha.rulp.ximpl.runtime.factor.XRFactorOutToFile;
import alpha.rulp.ximpl.runtime.factor.XRFactorPrint;
import alpha.rulp.ximpl.runtime.factor.XRFactorRef;
import alpha.rulp.ximpl.runtime.factor.XRFactorReturn;
import alpha.rulp.ximpl.runtime.factor.XRFactorSetq;
import alpha.rulp.ximpl.runtime.factor.XRFactorSizeOfList;
import alpha.rulp.ximpl.runtime.factor.XRFactorStrCat;
import alpha.rulp.ximpl.runtime.factor.XRFactorStrEqual;
import alpha.rulp.ximpl.runtime.factor.XRFactorStrEqualNoCase;
import alpha.rulp.ximpl.runtime.factor.XRFactorStrMatch;
import alpha.rulp.ximpl.runtime.factor.XRFactorStrStartsWith;
import alpha.rulp.ximpl.runtime.factor.XRFactorStrTrim;
import alpha.rulp.ximpl.runtime.factor.XRFactorStrTrimHead;
import alpha.rulp.ximpl.runtime.factor.XRFactorStrTrimTail;
import alpha.rulp.ximpl.runtime.factor.XRFactorSystemGC;
import alpha.rulp.ximpl.runtime.factor.XRFactorSystemTime;
import alpha.rulp.ximpl.runtime.factor.XRFactorToAtom;
import alpha.rulp.ximpl.runtime.factor.XRFactorToString;
import alpha.rulp.ximpl.runtime.factor.XRFactorTry;
import alpha.rulp.ximpl.runtime.factor.XRFactorTypeOf;
import alpha.rulp.ximpl.runtime.factor.XRFactorValueOf;
import alpha.rulp.ximpl.runtime.factor.XRFactorValueTypeOf;
import alpha.rulp.ximpl.runtime.factor.XRFactorWatchVar;
import alpha.rulp.ximpl.runtime.factor.XRFactorWhen;
import alpha.rulp.ximpl.thread.factor.XRFactorDoParallel;
import alpha.rulp.ximpl.thread.factor.XRFactorSleep;

public class XRLoadBaseObject implements IRObjectLoader {

	@Override
	public void load(IRInterpreter interpreter, IRFrame sysFrame) throws RException, IOException {

		// Objects
		RulpUtility.addFrameObject(sysFrame, O_Nil);
		RulpUtility.addFrameObject(sysFrame, O_True);
		RulpUtility.addFrameObject(sysFrame, O_False);
		RulpUtility.addFrameObject(sysFrame, O_Nan);
		RulpUtility.addFrameObject(sysFrame, T_Atom);
		RulpUtility.addFrameObject(sysFrame, T_Bool);
		RulpUtility.addFrameObject(sysFrame, T_Instance);
		RulpUtility.addFrameObject(sysFrame, T_Expr);
		RulpUtility.addFrameObject(sysFrame, T_Factor);
		RulpUtility.addFrameObject(sysFrame, T_Float);
		RulpUtility.addFrameObject(sysFrame, T_Func);
		RulpUtility.addFrameObject(sysFrame, T_Int);
		RulpUtility.addFrameObject(sysFrame, T_List);
		RulpUtility.addFrameObject(sysFrame, T_Macro);
		RulpUtility.addFrameObject(sysFrame, T_Native);
		RulpUtility.addFrameObject(sysFrame, T_Null);
		RulpUtility.addFrameObject(sysFrame, T_String);
		RulpUtility.addFrameObject(sysFrame, T_Var);

		RulpUtility.addFrameObject(sysFrame, new XRFactorNameOf(F_NAME_OF));
		RulpUtility.addFrameObject(sysFrame, new XRFactorTypeOf(F_TYPE_OF));
		RulpUtility.addFrameObject(sysFrame, new XRFactorValueOf(F_VALUE_OF));
		RulpUtility.addFrameObject(sysFrame, new XRFactorValueTypeOf(F_VALUE_TYPE_OF));
		RulpUtility.addFrameObject(sysFrame, new XRFactorAlias(F_ALIAS));
		RulpUtility.addFrameObject(sysFrame, new XRFactorEqual(F_EQUAL));
		RulpUtility.addFrameObject(sysFrame, new XRFactorNotEqual(F_NOT_EQUAL));
		RulpUtility.addFrameObject(sysFrame, new XRFactorToAtom(F_TO_ATOM));
		RulpUtility.addFrameObject(sysFrame, new XRFactorSizeOfList(F_SIZE_OF_LIST));

		RulpUtility.addFrameObject(sysFrame, new XRFactorClassOf(F_CLASS_OF));
		RulpUtility.addFrameObject(sysFrame, new XRFactorCreate(F_CREATE));
		RulpUtility.addFrameObject(sysFrame, new XRFactorDelete(F_DELETE));

		// IO
		RulpUtility.addFrameObject(sysFrame, new XRFactorPrint(F_PRINT));
		RulpUtility.addFrameObject(sysFrame, new XRFactorOutToFile(F_OUT_TO_FILE));
		RulpUtility.addFrameObject(sysFrame, new XRFactorLoad(F_LOAD));

		// Variable, Value & Expression
		RulpUtility.addFrameObject(sysFrame, new XRFactorDefvar(F_DEFVAR, true, false));
		RulpUtility.addFrameObject(sysFrame, new XRFactorSetq(F_SETQ));
		RulpUtility.addFrameObject(sysFrame, new XRFactorWatchVar(F_WATCH_VAR));
		RulpUtility.addFrameObject(sysFrame, new XRFactorRef(F_REF));

		// String
		RulpUtility.addFrameObject(sysFrame, new XRFactorToString(F_TO_STRING));
		RulpUtility.addFrameObject(sysFrame, new XRFactorStrCat(F_STRCAT));
		RulpUtility.addFrameObject(sysFrame, new XRFactorStrEqual(F_STR_EQUAL));
		RulpUtility.addFrameObject(sysFrame, new XRFactorStrEqualNoCase(F_STR_EQUAL_NOCASE));
		RulpUtility.addFrameObject(sysFrame, new XRFactorStrTrim(F_STR_TRIM));
		RulpUtility.addFrameObject(sysFrame, new XRFactorStrTrimHead(F_STR_TRIM_HEAD));
		RulpUtility.addFrameObject(sysFrame, new XRFactorStrTrimTail(F_STR_TRIM_TAIL));
		RulpUtility.addFrameObject(sysFrame, new XRFactorStrMatch(F_STR_MATCH));
		RulpUtility.addFrameObject(sysFrame, new XRFactorStrStartsWith(F_STR_START_WITH));

		// Arithmetic
		RulpUtility.addFrameObject(sysFrame, new XRFactorArithmetic(F_O_ADD, ArithmeticType.ADD));
		RulpUtility.addFrameObject(sysFrame, new XRFactorArithmetic(F_O_SUB, ArithmeticType.SUB));
		RulpUtility.addFrameObject(sysFrame, new XRFactorArithmetic(F_O_BY, ArithmeticType.BY));
		RulpUtility.addFrameObject(sysFrame, new XRFactorArithmetic(F_O_DIV, ArithmeticType.DIV));

		// Boolean
		RulpUtility.addFrameObject(sysFrame, new XRFactorBoolNot(F_B_NOT));
		RulpUtility.addFrameObject(sysFrame, new XRFactorBoolAnd(F_B_AND));
		RulpUtility.addFrameObject(sysFrame, new XRFactorBoolOr(F_B_OR));

		// Relational
		RulpUtility.addFrameObject(sysFrame, new XRFactorComparison(F_O_EQ, ComparisonType.Equal)); // =
		RulpUtility.addFrameObject(sysFrame, new XRFactorComparison(F_O_GT, ComparisonType.Bigger)); // >
		RulpUtility.addFrameObject(sysFrame, new XRFactorComparison(F_O_LT, ComparisonType.Smaller)); // <
		RulpUtility.addFrameObject(sysFrame, new XRFactorComparison(F_O_GE, ComparisonType.BiggerOrEqual)); // >=
		RulpUtility.addFrameObject(sysFrame, new XRFactorComparison(F_O_LE, ComparisonType.SmallerOrEqual));// <=

		// Macro & Function
		RulpUtility.addFrameObject(sysFrame, new XRFactorDefmacro(F_DEFMACRO));
		RulpUtility.addFrameObject(sysFrame, new XRFactorDefun(F_DEFUN));

		// Lambda
		RulpUtility.addFrameObject(sysFrame, new XRFactorLet(F_LET));
		RulpUtility.addFrameObject(sysFrame, new XRFactorLambda(F_LAMBDA));

		// Control
		RulpUtility.addFrameObject(sysFrame, new XRFactorIf(F_IF));
		RulpUtility.addFrameObject(sysFrame, new XRFactorWhen(F_WHEN));
		RulpUtility.addFrameObject(sysFrame, new XRFactorLoop(F_LOOP));
		RulpUtility.addFrameObject(sysFrame, new XRFactorReturn(F_RETURN));
		RulpUtility.addFrameObject(sysFrame, new XRFactorContinue(F_CONTINUE));
		RulpUtility.addFrameObject(sysFrame, new XRFactorDo(F_DO));

		// Exception
		RulpUtility.addFrameObject(sysFrame, new XRFactorError(F_E_ERROR));
		RulpUtility.addFrameObject(sysFrame, new XRFactorTry(F_E_TRY));
		RulpUtility.addFrameObject(sysFrame, new XRFactorGetErrValue(F_E_GET_ERR_VALUE));

		// Collection
		RulpUtility.addFrameObject(sysFrame, new XRFactorColJoin(F_JOIN));
		RulpUtility.addFrameObject(sysFrame, new XRFactorColUnion(F_UNION));
		RulpUtility.addFrameObject(sysFrame, new XRFactorForeach(F_FOREACH));
		RulpUtility.addFrameObject(sysFrame, new XRFactorGet(F_GET));

		// Thread
		RulpUtility.addFrameObject(sysFrame, new XRFactorSleep(F_T_SLEEP));
		RulpUtility.addFrameObject(sysFrame, new XRFactorDoParallel(F_DO_Parallel));

		// Time
		RulpUtility.addFrameObject(sysFrame, new XRFactorDate(F_DATE));

		// System
		RulpUtility.addFrameObject(sysFrame, new XRFactorSystemGC(F_SYS_GC));
		RulpUtility.addFrameObject(sysFrame, new XRFactorSystemTime(F_SYS_TIME));

		// Load base script
		RulpUtility.loadFromJar(interpreter, sysFrame, "alpha/resource/base.rulp", "utf-8");
	}

}
