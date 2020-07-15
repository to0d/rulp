package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorPrintTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(print (+ 1 2))", "3", "3");
		_test("(print \"xyz\")", "\"xyz\"", "xyz");
		_test("(print \"a\\nb\")", "\"a\\nb\"", "a\nb");
		_test("(print (+ 1 2) \"a\")", "\"a\"", "3a");
		
		_setup();
		_test("(defvar x 1)", "&x");
		_test("(print x)", "1", "1");
	}
}
