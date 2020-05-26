/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.A_NIL;

import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;

public class XRFunctionLambda extends XRFunction implements IRFunction {

	protected IRFrame lambdaFrame;

	public XRFunctionLambda(List<String> paraNmeList, List<IRAtom> paraTypeList, IRList funBody, IRFrame lambdaFrame) {
		super(A_NIL, paraNmeList, paraTypeList, funBody, null);
		this.lambdaFrame = lambdaFrame;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
		return super.compute(args, interpreter, lambdaFrame);
	}

}