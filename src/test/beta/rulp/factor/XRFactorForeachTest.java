package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorForeachTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(foreach (?a '(1 2 3)) (+ ?a 1))", "'(2 3 4)");

	}

	@Test
	void test2() {

		_setup();
		_test("(foreach (?a '(1 2 3)) (+ ?a 1) (+ ?a 2))", "'(3 4 5)");
	}

	@Test
	void test3() {

		_setup();
		_test("(foreach (?a '(1 2 3 4 5)) (if (> ?a 3) (return 1)) (return ?a))", "'(1 2 3 1 1)");
	}

	@Test
	void test4() {

		_setup();
		_test("(foreach (?a '(1 2 3 4 5)) (if (> ?a 3) (continue)) (return ?a))", "'(1 2 3)");
	}
}
