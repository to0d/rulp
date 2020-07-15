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
import java.util.Iterator;
import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RReturn;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFunction implements IRFunction {

	protected final int argCount;

	protected String description;

	protected IRList funBody;

	protected String name;

	protected final List<String> paraNameList;

	protected final List<IRAtom> paraTypeList;

	protected String signature;

	public XRFunction(String name, List<String> paraNameList, List<IRAtom> paraTypeList, IRList funBody,
			String description) {

		this.name = name;
		this.description = description;
		this.paraNameList = new ArrayList<>(paraNameList);
		this.argCount = paraNameList.size();
		this.paraTypeList = new ArrayList<>();

		for (int i = 0; i < argCount; ++i) {

			IRAtom paraType = null;
			if (paraTypeList != null && i < paraTypeList.size()) {
				paraType = paraTypeList.get(i);
			}

			if (paraType == null) {
				paraType = O_Nil;
			}

			this.paraTypeList.add(paraType);
		}

		this.funBody = funBody;
	}

	private void _matchTypeList(IRList args) throws RException {

		if ((argCount + 1) != args.size()) {
			throw new RException("Invalid parameter count: " + argCount);
		}

		Iterator<IRAtom> typeIter = paraTypeList.iterator();
		IRIterator<? extends IRObject> valIter = args.listIterator(1);

		int argIndex = 0;

		while (typeIter.hasNext()) {

			IRAtom typeAtom = typeIter.next();
			IRObject valObj = valIter.next();

			// Match any type
			if (typeAtom == O_Nil) {
				continue;
			}

			IRAtom argAtom = RulpUtility.getObjectType(valObj);
			if (typeAtom != argAtom) {
				throw new RException(String.format("the type<%s> of %d argument<%s> not match <%s>", argAtom, argIndex,
						valObj, typeAtom));
			}

			++argIndex;
		}
	}

	@Override
	public String asString() {
		return name;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		_matchTypeList(args);

		IRFrame funFrame = RulpFactory.createdChildFrame(frame, "FUN");

		{
			IRIterator<? extends IRObject> argIter = args.listIterator(1); // Skip factor head element

			Iterator<String> paraIter = paraNameList.iterator();
			while (argIter.hasNext()) {

				String para = paraIter.next();
				IRObject arg = argIter.next();
				if (arg.getType() != RType.VAR) {
					arg = RulpFactory.createVar(para, arg);
				}

				funFrame.setEntry(para, arg);
			}
		}

		try {

			IRObject rst = null;
			IRIterator<? extends IRObject> iter = funBody.iterator();
			while (iter.hasNext()) {
				rst = interpreter.compute(funFrame, iter.next());
			}
			return rst;

		} catch (RReturn r) {
			return r.getReturnValue();
		}

	}

	@Override
	public int getArgCount() {
		return argCount;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IRAtom> getParaTypeList() {
		return paraTypeList;
	}

	@Override
	public String getSignature() throws RException {

		if (signature == null) {

			ArrayList<IRObject> signatureObjs = new ArrayList<>();

			signatureObjs.add(RulpFactory.createAtom(name));
			signatureObjs.addAll(getParaTypeList());

			signature = RulpUtility.toString(RulpFactory.createList(signatureObjs));
		}

		return signature;
	}

	@Override
	public RType getType() {
		return RType.FUNC;
	}

	public boolean isThreadSafe() {
		return true;
	}

	public String toString() {
		try {
			return this.getSignature();
		} catch (RException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

}