package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorValueOfTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(value-of nil)", "nil");
		_test("(value-of true)", "true");
		_test("(value-of 1)", "1");
		_test("(value-of 1.1)", "1.1");
		_test("(value-of '(a b))", "'(a b)");
		_test("(value-of print-list)", "print-list");
		_test("(value-of \"abc\")", "\"abc\"");

		_test("(defvar var 10)(value-of var)", "&var 10");
	}

}
