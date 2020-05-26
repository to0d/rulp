package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorSetqTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(defvar x 10) x", "&x 10");
		_test("(setq x 9) x", "&x 9");

	}

	@Test
	void test_err1() {

		_setup();
		_test_error("(setq x 10)", "var not found: x");
	}

	@Test
	void test_err2() {

		_setup();
		_test_error("(setq x)", "Invalid parameters: (setq x)");
	}

}
