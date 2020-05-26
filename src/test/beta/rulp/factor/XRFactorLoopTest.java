package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorLoopTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(defvar x) (setq x 5)", "&x &x");
		_test("(loop (setq x (- x 1)) (print x) (print ,) (when (< x 1) (return x)))", "0", "4,3,2,1,0,");
		_test("(loop for x in '(1 2 3) do (print x) (print ,))", "nil", "1,2,3,");
		_test("(loop for x from 1 to 3 do (print x) (print ,))", "nil", "1,2,3,");
		_test("(loop for x from 3 to 1 do (print x) (print ,))", "nil", "");
		_test("(loop for x from (+ 1 2) to (- 6 2) do (print x) (print ,))", "nil", "3,4,");

	}

	@Test
	void test2() {
		_test("(loop for x in '(1 2 3 4) do (if (= x 3) (continue)) (print x) (print ,))", "nil", "1,2,4,");
		_test("(loop for x in '(1 2 3 4) do (if (= x 3) (return)) (print x) (print ,))", "nil", "1,2,");
	}

}
