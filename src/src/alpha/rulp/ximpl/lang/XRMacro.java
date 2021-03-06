/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.utility.RulpFactory;

public class XRMacro implements IRMacro {

	static class MacroUpdateIterator implements IRIterator<IRObject> {

		private IRIterator<? extends IRObject> iter;
		private Map<String, IRObject> macroMap;

		public MacroUpdateIterator(IRIterator<? extends IRObject> iter, Map<String, IRObject> macroMap) {
			super();
			this.iter = iter;
			this.macroMap = macroMap;
		}

		@Override
		public boolean hasNext() throws RException {
			return iter.hasNext();
		}

		@Override
		public IRObject next() throws RException {
			return updateMacroObj(iter.next(), macroMap);
		}

	}

	public static IRObject updateMacroObj(IRObject obj, Map<String, IRObject> macroMap) throws RException {

		if (obj == null) {
			return obj;
		}

		switch (obj.getType()) {
		case ATOM:
			IRAtom atom = (IRAtom) obj;
			IRObject mv = macroMap.get(atom.getName());
			return mv == null ? obj : mv;

		case EXPR:
			return RulpFactory.createExpression(new MacroUpdateIterator(((IRList) obj).iterator(), macroMap));

		case LIST:
			return RulpFactory.createList(new MacroUpdateIterator(((IRList) obj).iterator(), macroMap));

		default:
			return obj;
		}
	}

	private String _macroSignature;

	protected IRList bodyList;

	protected String description;

	protected String macroName;

	protected List<String> paraNmeList;

	public XRMacro(String macroName, List<String> paraNmeList, IRList bodyList, String description) {
		this.macroName = macroName;
		this.description = description;
		this.paraNmeList = paraNmeList;
		this.bodyList = bodyList;
	}

	@Override
	public String asString() {
		return macroName;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter intepreter, IRFrame frame) throws RException {

		if (args.size() != (paraNmeList.size() + 1)) {
			throw new RException("Invalid parameters: " + args);
		}

		Map<String, IRObject> macroMap = new HashMap<>();
		{
			IRIterator<? extends IRObject> valueIter = args.listIterator(1); // Skip factor head element
			Iterator<String> paraIter = paraNmeList.iterator();
			while (valueIter.hasNext()) {
				macroMap.put(paraIter.next(), valueIter.next());
			}
		}

		return intepreter.compute(frame, updateMacroObj(bodyList, macroMap));
	}

	@Override
	public String getName() {
		return macroName;
	}

	@Override
	public String getSignature() {

		if (_macroSignature == null) {

		}

		return null;
	}

	@Override
	public RType getType() {
		return RType.MACRO;
	}

	public boolean isThreadSafe() {
		return true;
	}

	@Override
	public String toString() {
		return macroName;
	}
}
