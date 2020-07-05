/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utility;

import static alpha.rulp.lang.Constant.A_NIL;
import static alpha.rulp.lang.Constant.F_LS;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRError;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRNative;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.lang.XRError;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.runtime.IRTokener;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.runtime.RName;
import alpha.rulp.ximpl.lang.XRAtom;
import alpha.rulp.ximpl.lang.XRBoolean;
import alpha.rulp.ximpl.lang.XRFloat;
import alpha.rulp.ximpl.lang.XRInteger;
import alpha.rulp.ximpl.lang.XRIteratorAdatper;
import alpha.rulp.ximpl.lang.XRListArray;
import alpha.rulp.ximpl.lang.XRListBuilderIterator;
import alpha.rulp.ximpl.lang.XRListIterator;
import alpha.rulp.ximpl.lang.XRListIteratorR;
import alpha.rulp.ximpl.lang.XRListList;
import alpha.rulp.ximpl.lang.XRMacro;
import alpha.rulp.ximpl.lang.XRNative;
import alpha.rulp.ximpl.lang.XRString;
import alpha.rulp.ximpl.lang.XRVar;
import alpha.rulp.ximpl.runtime.XRFrame;
import alpha.rulp.ximpl.runtime.XRFunction;
import alpha.rulp.ximpl.runtime.XRFunctionLambda;
import alpha.rulp.ximpl.runtime.XRFunctionList;
import alpha.rulp.ximpl.runtime.XRInterpreter;
import alpha.rulp.ximpl.runtime.XRLoadBaseObject;
import alpha.rulp.ximpl.runtime.XRLoadJvmObject;
import alpha.rulp.ximpl.runtime.XRParser;
import alpha.rulp.ximpl.runtime.XRTokener;
import alpha.rulp.ximpl.runtime.factor.XRFactorLs;

public class RulpFactory {

	public static final IRExpr EMPTY_EXPR;

	public static final IRList EMPTY_LIST;

	protected static final XRBoolean False = new XRBoolean(false);

	protected static AtomicInteger frameCount = new AtomicInteger(0);

	static List<IRObjectLoader> rulpLoaders = new LinkedList<>();

	protected static final XRBoolean True = new XRBoolean(true);

	protected static AtomicInteger unNameframeCount = new AtomicInteger(0);

	static {
		EMPTY_LIST = new XRListList(Collections.<IRObject>emptyList(), RType.LIST);
		EMPTY_EXPR = new XRListList(Collections.<IRObject>emptyList(), RType.EXPR);
	}

	static {

		// Base Loader
		registerLoader(new XRLoadBaseObject());
		registerLoader(new XRLoadJvmObject());
	}

	protected static XRFrame _createSystemFrame() {

		return new XRFrame(null, "SYS", 0, getNextFrameId()) {

			@Override
			public IRFrameEntry removeEntry(String name) throws RException {
				throw new RException("unable to remove object in RootFrame");
			}
		};
	}

	public static IRAtom createAtom(RName rname) {
		return new XRAtom(rname.fullName, rname);
	}

	public static IRAtom createAtom(String name) {
		return new XRAtom(name);
	}

	public static IRBoolean createBoolean(boolean value) {
		return value ? True : False;
	}

	public static IRFrame createdChildFrame(IRFrame parentFrame) {
		return createdChildFrame(parentFrame);
	}

	public static IRFrame createdChildFrame(IRFrame parentFrame, String name) {

		if (name == null) {
			name = String.format("frame-%d", unNameframeCount.getAndIncrement());
		}

		return new XRFrame(parentFrame, name, parentFrame.getLevel() + 1, getNextFrameId());
	}

	public static IRError createError(IRAtom id, IRObject value) {
		return new XRError(id, value);
	}

	public static IRExpr createExpression() {
		return EMPTY_EXPR;
	}

	public static IRExpr createExpression(IRIterator<? extends IRObject> iter) throws RException {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_EXPR;
		}

		return new XRListIteratorR(iter, RType.EXPR);
	}

	public static IRExpr createExpression(IRObject... elements) {
		return new XRListArray(elements, RType.EXPR);
	}

	public static IRExpr createExpression(Iterator<? extends IRObject> iter) {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_EXPR;
		}

		return new XRListIterator(iter, RType.EXPR);
	}

	public static IRExpr createExpression(List<? extends IRObject> list) {

		if (list == null) {
			return EMPTY_EXPR;
		}

		return new XRListList(list, RType.EXPR);
	}

	public static IRFloat createFloat(float value) {
		return new XRFloat(value);
	}

	public static IRFunction createFunction(String funName, List<String> paraNames, List<IRAtom> paraTypes,
			IRList funBody, String description) {
		return new XRFunction(funName, paraNames, paraTypes, funBody, description);
	}

	public static IRFunction createFunctionLambda(List<String> paraNames, List<IRAtom> paraTypes, IRList funBody,
			IRFrame lambdaFrame) {
		return new XRFunctionLambda(paraNames, paraTypes, funBody, lambdaFrame);
	}

	public static IRFunctionList createFunctionList(String funName) {
		return new XRFunctionList(funName);
	}

	public static IRInteger createInteger(int value) {
		return new XRInteger(value);

	}

	public static IRInterpreter createInterpreter() throws RException, IOException {

		XRInterpreter interpreter = new XRInterpreter();

		/******************************************************/
		// Init Parser
		/******************************************************/
		interpreter.setParser(RulpFactory.createParser());

		/******************************************************/
		// Init Frame
		/******************************************************/
		XRFrame sysFrame = _createSystemFrame();
		interpreter.setSystemFrame(sysFrame);

		/******************************************************/
		// Load object
		/******************************************************/
		for (IRObjectLoader loader : rulpLoaders) {
			loader.load(interpreter, sysFrame);
		}

		// Add this at the end
		RulpUtility.addFrameObject(sysFrame, new XRFactorLs(F_LS, sysFrame));

		return interpreter;
	}

	public static IRList createList() {
		return EMPTY_LIST;
	}

	public static IRList createList(IRIterator<? extends IRObject> iter) throws RException {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_LIST;
		}

		return new XRListIteratorR(iter, RType.LIST);
	}

	public static IRList createList(IRObject... elements) {
		return new XRListArray(elements, RType.LIST);
	}

	public static IRList createList(Iterator<? extends IRObject> iter) {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_LIST;
		}

		return new XRListIterator(iter, RType.LIST);
	}

	public static IRList createList(List<? extends IRObject> list) {

		if (list == null) {
			return EMPTY_LIST;
		}

		return new XRListList(list, RType.LIST);
	}

	public static IRList createListOfString(Collection<String> collection) {

		return new XRListBuilderIterator<String>(collection.iterator(), (str) -> {
			return RulpFactory.createString(str);
		}, RType.LIST);
	}

	public static IRMacro createMacro(String macroName, List<String> paraNames, IRList macroBody, String description) {
		return new XRMacro(macroName, paraNames, macroBody, description);
	}

	public static IRNative createNative(Object obj) {
		return new XRNative(obj);
	}

	public static IRAtom createNil() {

		return new XRAtom(A_NIL) {

			@Override
			public RType getType() {
				return RType.NIL;
			}

		};
	}

	public static IRParser createParser() {
		return new XRParser(createTokener());
	}

	public static <T> IRIterator<T> createRIterator(Iterator<T> iter) throws RException {
		return new XRIteratorAdatper<T>(iter);
	}

	public static IRString createString(String value) {

		if (value == null) {
			return null;
		}

		return new XRString(value);
	}

	public static IRTokener createTokener() {
		return new XRTokener();
	}

	public static IRVar createVar(String name) {
		return new XRVar(name, O_Nil);
	}

	public static IRVar createVar(String name, IRObject val) {
		return new XRVar(name, val);
	}

	public static int getNextFrameId() {
		return frameCount.getAndIncrement();
	}

	public static void registerLoader(IRObjectLoader loader) {
		rulpLoaders.add(loader);
	}
}
