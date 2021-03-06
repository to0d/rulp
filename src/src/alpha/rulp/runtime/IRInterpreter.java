/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.runtime;

import java.util.List;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRInterpreter {

	public void addObject(IRObject obj) throws RException;

	public IRObject compute(IRFrame frame, IRObject obj) throws RException;;

	public List<IRObject> compute(String input) throws RException;

	public IRObject getObject(String name) throws RException;

	public IROut getOut();

	public IRParser getParser();

	public IRFrame getRuntimeFrame();

	public void out(String line);

	public void setOutput(IROut out);
}
