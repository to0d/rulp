/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.RulpUtility;

public class XRLoadJvmObject implements IRObjectLoader {

	static Map<String, String> jvmVarMap = new HashMap<>();

	private static void _mappingJvmVar(IRFrame sysFrame, String varName, String jvmVarName)
			throws RException, IOException {

		String varVal = null;

		synchronized (jvmVarMap) {
			varVal = jvmVarMap.get(varName);
			if (varVal == null) {
				varVal = System.getProperty(jvmVarName);
				jvmVarMap.put(varName, varVal);
			}
		}

		RulpUtility.addFrameObject(sysFrame, RulpFactory.createVar(varName, RulpFactory.createString(varVal)));
	}

	@Override
	public void load(IRInterpreter interpreter, IRFrame sysFrame) throws RException, IOException {

		// Load JVM Var
		_mappingJvmVar(sysFrame, "?user.home", "user.home");
		_mappingJvmVar(sysFrame, "?user.name", "user.name");
		_mappingJvmVar(sysFrame, "?user.dir", "user.dir");
		_mappingJvmVar(sysFrame, "?file.separator", "file.separator");
		_mappingJvmVar(sysFrame, "?path.separator", "path.separator");
		_mappingJvmVar(sysFrame, "?os.name", "os.name");
		_mappingJvmVar(sysFrame, "?os.version", "os.version");
	}

}
