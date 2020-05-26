package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorArithmeticTest extends RulpTestBase {

	@Test
	void test() {
		_test("(+ 1 2 3)", "6");
		_test("(+ 1 (+ 2 3))", "6");
		_test("(+ 1 2)", "3");
	}

}
