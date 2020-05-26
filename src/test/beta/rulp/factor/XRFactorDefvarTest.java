package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorDefvarTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(defvar x 10) x", "&x 10");
		_test("(defvar y) y", "&y nil");
		_test("(ls)", "'(&x &y)");

	}

	@Test
	void test2() {

		// defvar does assignment only once
		_setup();
		_test("(defvar x 10) x", "&x 10");
		_test("(defvar x 9) x", "&x 10");
	}
}
