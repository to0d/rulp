/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.runtime;

import java.util.Iterator;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRFrame {

	public IRVar addVar(String name) throws RException;

	public IRFrameEntry getEntry(String name) throws RException;

	public int getFrameID();

	public String getFrameName();

	public int getLevel();

	public IRObject getObject(String name) throws RException;

	public IRFrame getParentFrame();

	public Iterator<IRFrameEntry> listEntries();

	public IRFrameEntry removeEntry(String name) throws RException;

	public IRFrameEntry setEntry(String name, IRObject obj) throws RException;

	public void setEntryAliasName(IRFrameEntry entry, String aliasName) throws RException;

	public void setThreadContext(IRThreadContext context);

	public IRThreadContext getThreadContext();

}
