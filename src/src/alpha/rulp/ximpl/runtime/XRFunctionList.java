/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFunctionList implements IRFunctionList {

	static class FuncList {

		static boolean isDefaultFun(IRFunction fun) {

			List<IRAtom> paraTypes = fun.getParaTypeList();
			int argCount = fun.getArgCount();
			for (int i = 0; i < argCount; ++i) {

				if (paraTypes.get(i) != O_Nil) {
					return false;
				}
			}

			return true;
		}

		public IRFunction defaultFun;

		public List<IRFunction> funcList = new LinkedList<>();

		public Map<String, IRFunction> signatureMap = new HashMap<>();

		public void addFunc(IRFunction fun) throws RException {

			if (isDefaultFun(fun)) {

				if (this.defaultFun != null) {
					throw new RException("Duplicate default function found: " + fun.getSignature());
				}

				this.defaultFun = fun;

			} else {

				String signature = fun.getSignature();

				if (this.signatureMap.containsKey(signature)) {
					throw new RException("Duplicate function found: " + signature);
				}

				this.signatureMap.put(signature, fun);
				this.funcList.add(fun);
			}
		}

		public IRFunction findMatchFun(IRList args) throws RException {

			IRFunction matchFun = null;

			for (IRFunction fun : funcList) {

				if (_matchTypeList(fun.getParaTypeList(), args)) {

					if (matchFun == null) {
						matchFun = fun;

					} else {
						throw new RException(String.format("ambiguous funcion found: fun1=%s, fun2=%s, expr=%s",
								matchFun, fun, args));
					}
				}
			}

			if (matchFun == null) {
				matchFun = defaultFun;
			}

			return matchFun;
		}
	}

	static boolean _matchTypeList(List<IRAtom> argTypes, IRList args) throws RException {

		if ((argTypes.size() + 1) != args.size()) {
			return false;
		}

		Iterator<IRAtom> typeIter = argTypes.iterator();
		IRIterator<? extends IRObject> valIter = args.listIterator(1);

		while (typeIter.hasNext()) {

			IRAtom typeAtom = typeIter.next();
			IRObject valObj = valIter.next();

			// Match any type
			if (typeAtom == O_Nil) {
				continue;
			}

			if (typeAtom != RulpUtility.getObjectType(valObj)) {
				return false;
			}
		}

		return true;
	}

	protected List<IRFunction> allFuncList = new LinkedList<>();

	protected Map<Integer, FuncList> funListMap = new HashMap<>();

	protected String name;

	protected String signature;

	public XRFunctionList(String name) {
		super();
		this.name = name;
	}

	@Override
	public void addFunc(IRFunction fun) throws RException {

		int argCount = fun.getArgCount();

		FuncList funcList = funListMap.get(argCount);
		if (funcList == null) {
			funcList = new FuncList();
			funListMap.put(argCount, funcList);
		}

		funcList.addFunc(fun);
		allFuncList.add(fun);
		signature = null;
	}

	@Override
	public String asString() {
		return name;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		int argCount = args.size() - 1;
		FuncList funcList = funListMap.get(argCount);
		if (funcList == null) {
			throw new RException(String.format("match funcion not found:  expr=%s", args));
		}

		IRFunction matchFun = funcList.findMatchFun(args);
		if (matchFun == null) {
			throw new RException(String.format("match funcion not found:  expr=%s", args));
		}

		return RuntimeUtils.computeCallable(matchFun, args, interpreter, frame);
	}

	@Override
	public int getArgCount() {
		return -1;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IRAtom> getParaTypeList() {
		return null;
	}

	@Override
	public String getSignature() throws RException {

		if (signature == null) {

			ArrayList<String> allSignatures = new ArrayList<>();

			for (IRFunction func : allFuncList) {
				allSignatures.add(func.getSignature());
			}

			Collections.sort(allSignatures);
			signature = RulpUtility.toString(RulpFactory.createListOfString(allSignatures));
		}

		return signature;
	}

	@Override
	public RType getType() {
		return RType.FUNC;
	}

	public boolean isThreadSafe() {
		return false;
	}

	public String toString() {
		return name;
	}
}
