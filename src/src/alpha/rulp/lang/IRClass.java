/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;

public interface IRClass extends IRObject {

	public IRAtom getClassAtom();

	public IRInstance createInstance(String instanceName, IRList args, IRInterpreter interpreter, IRFrame frame)
			throws RException;

	public void destroyInstance(IRInstance instance, IRInterpreter interpreter, IRFrame frame) throws RException;
}
