package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorLetTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(defvar x)", "&x");
		_test("(ls *)", "'(&x)");
		_test("(let (a 5) (+ a 1))", "6");

		// no global var created
		_test("(ls *)", "'(&x)");
	}

}
