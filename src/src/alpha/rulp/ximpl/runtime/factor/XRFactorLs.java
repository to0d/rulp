/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRFactorLs extends AbsRFactorAdapter implements IRFactor {

	private IRFrame sysFrame;

	private List<IRObject> sysObjList = new ArrayList<>();

	private Set<IRObject> sysObjSet = new HashSet<>();

	public XRFactorLs(String factorName, IRFrame sysFrame) {

		super(factorName);

		Iterator<IRFrameEntry> eIter = sysFrame.listEntries();
		while (eIter.hasNext()) {
			IRObject obj = eIter.next().getObject();
			sysObjSet.add(obj);
		}

		sysObjSet.add(this);

		sysObjList.addAll(sysObjSet);
		Collections.sort(sysObjList, (a, b) -> {
			return a.asString().compareTo(b.asString());
		});

		sysObjList = Collections.unmodifiableList(sysObjList);
	}

	private boolean _isSystemObject(IRObject obj) {
		return sysObjSet.contains(obj);
	}

	private void _listObjs(Map<String, IRObject> objMap, IRFrame frame) {

		Iterator<IRFrameEntry> eIter = frame.listEntries();
		while (eIter.hasNext()) {

			IRFrameEntry entry = eIter.next();
			String objName = entry.getName();
			IRObject obj = null;

			if (!objMap.containsKey(objName) && !_isSystemObject(obj = entry.getObject())) {
				objMap.put(objName, obj);
			}
		}
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 1 && args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		boolean lsAll = false;
		boolean lsSys = false;
		boolean lsEnv = false;
		int frameLevel = 0;

		if (args.size() == 2) {

			IRObject opt = args.get(1);

			if (RulpUtility.isAtom(opt, "sys")) {
				lsSys = true;

			} else if (RulpUtility.isAtom(opt, "env")) {
				lsEnv = true;

			} else if (RulpUtility.isAtom(opt, "*")) {
				lsAll = true;

			} else {
				frameLevel = RulpUtility.asInteger(interpreter.compute(frame, opt)).asInteger();
				if (frameLevel < 0) {
					throw new RException("Invalid frame level: " + opt);
				}
			}
		}

		if (lsSys) {
			return RulpFactory.createList(sysObjList);
		}

		Map<String, IRObject> objMap = new HashMap<>();

		if (lsAll) {

			for (IRFrame curFrame = frame; curFrame != sysFrame; curFrame = curFrame.getParentFrame()) {
				_listObjs(objMap, curFrame);
			}

		} else if (lsEnv) {

			_listObjs(objMap, interpreter.getRuntimeFrame());
			return RulpFactory.createList(sysObjList);

		} else {

			IRFrame curFrame = frame;
			int curFrameIndex = 0;
			while (curFrameIndex < frameLevel && curFrame != null) {
				curFrame = curFrame.getParentFrame();
				curFrameIndex++;
			}

			if (curFrame != null) {
				_listObjs(objMap, frame);
			}
		}

		List<IRObject> list = new LinkedList<>(objMap.values());
		Collections.sort(list, (a, b) -> {
			return a.asString().compareTo(b.asString());
		});

		return RulpFactory.createList(list);
	}

	public boolean isThreadSafe() {
		return false;
	}
}
