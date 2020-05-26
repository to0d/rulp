/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.utility.RulpFactory;

public class XRFrame implements IRFrame {

	static class EntryNode {
		IRFrameEntry entry = null;
		boolean isLocal = false;
	}

	static class XRFrameEntry implements IRFrameEntry {

		private List<String> aliasNames = null;

		private IRFrame frame;
		private String name;
		private IRObject object;

		public XRFrameEntry(IRFrame frame, String name, IRObject object) {
			super();

			this.frame = frame;
			this.name = name;
			this.object = object;
		}

		public void addAliasName(String aliasName) {

			if (aliasNames == null) {
				aliasNames = new LinkedList<>();
			}

			aliasNames.add(aliasName);
		}

		@Override
		public List<String> getAliasName() {
			return aliasNames == null ? Collections.emptyList() : aliasNames;
		}

		@Override
		public IRFrame getFrame() {
			return frame;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public IRObject getObject() {
			return object;
		}

		public void setObject(IRObject object) {
			this.object = object;
		}

		public String toString() {
			return String.format("[name=%s, obj=%s, alias=%s]", name, object.toString(), getAliasName().toString());
		}

	}

	protected List<IRFrameEntry> _entryCacheList = null;

	protected Map<String, EntryNode> _nodeCatchMap = null;

	protected Map<String, IRFrameEntry> entryMap = null;

	protected int frameId;

	protected String frameName;

	protected IRFrame parentFrame = null;

	protected int stackLevel;

	public XRFrame(IRFrame parentFrame, String name, int frameId, int stackLevel) {
		super();
		this.parentFrame = parentFrame;
		this.stackLevel = stackLevel;
		this.frameName = name;
		this.frameId = frameId;
	}

	protected IRFrameEntry _findLocalEntry(String name) {

		IRFrameEntry entry = null;

		if (entryMap == null) {
			entryMap = new HashMap<>();
		} else {
			entry = entryMap.get(name);
		}

		return entry;
	}

	protected EntryNode _findNode(String name) {

		EntryNode entryNode = null;

		if (_nodeCatchMap == null) {
			_nodeCatchMap = new HashMap<>();
		} else {
			entryNode = _nodeCatchMap.get(name);
		}

		return entryNode;
	}

	@Override
	public IRVar addVar(String name) throws RException {
		IRVar var = RulpFactory.createVar(name);
		this.setEntry(name, var);
		return var;
	}

	@Override
	public IRFrameEntry getEntry(String name) throws RException {

		EntryNode entryNode = _findNode(name);
		if (entryNode == null) {

			entryNode = new EntryNode();

			IRFrameEntry localEntry = _findLocalEntry(name);
			if (localEntry != null) {
				entryNode.isLocal = true;
				entryNode.entry = localEntry;

			} else if (parentFrame != null) {
				entryNode.entry = parentFrame.getEntry(name);
				entryNode.isLocal = false;
			}

			_nodeCatchMap.put(name, entryNode);
		}

		return entryNode.entry;
	}

	@Override
	public int getFrameID() {
		return frameId;
	}

	@Override
	public String getFrameName() {
		return frameName;
	}

	@Override
	public int getLevel() {
		return stackLevel;
	}

	@Override
	public IRObject getObject(String name) throws RException {
		IRFrameEntry entry = getEntry(name);
		return entry == null ? null : entry.getObject();
	}

	@Override
	public IRFrame getParentFrame() {
		return parentFrame;
	}

	@Override
	public Iterator<IRFrameEntry> listEntries() {

		if (entryMap == null || entryMap.isEmpty()) {
			return Collections.<IRFrameEntry>emptyList().iterator();
		}

		_entryCacheList = new LinkedList<>();
		for (Entry<String, IRFrameEntry> e : entryMap.entrySet()) {

			String name = e.getKey();
			IRFrameEntry entry = e.getValue();

			// ignore alias entries
			if (!entry.getName().equals(name)) {
				continue;
			}

			_entryCacheList.add(entry);
		}

		return _entryCacheList.iterator();
	}

	@Override
	public IRFrameEntry removeEntry(String name) throws RException {

		IRFrameEntry entry = entryMap.remove(name);

		if (_nodeCatchMap != null) {
			_nodeCatchMap.remove(name);
		}

		if (entry != null) {
			_entryCacheList = null;
		}

		return entry;
	}

	@Override
	public IRFrameEntry setEntry(String name, IRObject obj) throws RException {

		IRFrameEntry localEntry = _findLocalEntry(name);

		// New entry
		if (localEntry == null) {
			localEntry = new XRFrameEntry(this, name, obj);

			entryMap.put(name, localEntry);

			if (_entryCacheList != null) {
				_entryCacheList.add(localEntry);
			}

			if (_nodeCatchMap != null) {
				_nodeCatchMap.remove(name);
			}
		}
		// Update entry
		else {
			((XRFrameEntry) localEntry).setObject(obj);
		}

		return localEntry;
	}

	@Override
	public void setEntryAliasName(IRFrameEntry entry, String aliasName) throws RException {

		// Check alias name
		IRFrameEntry localEntry = _findLocalEntry(aliasName);
		if (localEntry != null) {
			throw new RException(String.format("the name %s is already defined: %s", aliasName, localEntry));
		}

		entryMap.put(aliasName, entry);
		if (_nodeCatchMap != null) {
			_nodeCatchMap.remove(aliasName);
		}

		((XRFrameEntry) entry).addAliasName(aliasName);
	}

	public String toString() {
		return "Frame#" + frameName + "-" + frameId;
	}
}