/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IROut;

public class RulpTestBase {

	protected static class XROut implements IROut {

		StringBuffer sb = new StringBuffer();

		public void clear() {
			sb.setLength(0);
		}

		public String getOut() {
			return sb.toString();
		}

		@Override
		public void out(String line) {
			sb.append(line);
		}
	}

	protected static String _load(String path) {

		try {
			return StringUtil.toOneLine(FileUtil.openTxtFile(path));
		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
			return null;
		}
	}

	protected IRInterpreter _interpreter;

	protected XROut out = null;

	protected IRInterpreter _createInterpreter() throws RException, IOException {
		return RulpFactory.createInterpreter();
	}

	protected IRInterpreter _getInterpreter() throws RException, IOException {

		if (_interpreter == null) {
			_interpreter = _createInterpreter();
			out = new XROut();
			_interpreter.setOutput(out);
		}

		return _interpreter;
	};

	protected void _setup() {
		_interpreter = null;
	}

	protected void _test(String input) {
		_test(input, null, null);
	}

	protected void _test(String input, String expectResult) {
		_test(input, expectResult, "");
	}

	protected void _test(String input, String expectResult, String expectOutput) {

		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();

			List<IRObject> result = interpreter.compute(input);
			String output = out.getOut();

			if (expectResult != null) {
				assertEquals(String.format("input=%s", input), expectResult, RulpUtility.toString(result));
			}

			if (expectOutput != null) {
				assertEquals(String.format("input=%s", input), expectOutput, output);
			}

		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	protected void _test_error(String input, String expectError) {

		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();
			interpreter.compute(input);
			fail("Should fail: " + input);

		} catch (RException | IOException e) {
			assertEquals(String.format("input=%s", input), expectError, e.getMessage());
		}
	}

	protected void _test_script(String scriptPath) {

		String input = null;
		String result = null;

		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();

			for (String line : FileUtil.openTxtFile(scriptPath)) {

				if ((line = line.trim()).isEmpty() || line.startsWith(";;")) {
					continue;
				}

				if (line.startsWith(";END")) {
					return;
				}

				if (line.startsWith(";=>")) {

					if (input == null) {
						throw new RException("Input not found: " + scriptPath);
					}

					String expectResult = line.substring(";=>".length());
					assertEquals(String.format("input=%s, path=%s", input, scriptPath), expectResult, result);

				} else {
					input = line;
					result = RulpUtility.toString(interpreter.compute(input));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(String.format("error found in line: %s, file=%s", input, scriptPath));
		}
	}
}
