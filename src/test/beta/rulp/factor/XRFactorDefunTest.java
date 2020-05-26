package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorDefunTest extends RulpTestBase {

	@Test
	void test_fun_1() {
		_setup();
		_test("(defun fun1 (?v1 ?v2) \"a fun description\" (?v1 ?v2 3)) (fun1 + 1)", "'(fun1 nil nil) 4");
		_test("(defun fun2 (?v1 ?v2) \"a fun description\" (?v1 ?v2 3) (?v1 ?v2 4)) (fun2 + 1)", "'(fun2 nil nil) 5");
	}

	@Test
	void test_fun_ref_1() {

		_setup();

		_test("(defvar x 10) x", "&x 10");
		_test("(defun fun1 (?p1) (setq ?p1 (+ ?p1 1)))", "'(fun1 nil)");

		// pass value
		_test("(fun1 x)");
		_test("x", "10");
	}

	@Test
	void test_fun_ref_2() {

		_setup();
		_test("(defvar x 10) x", "&x 10");
		_test("(defun fun1 (?p1) (setq ?p1 (+ ?p1 1)))", "'(fun1 nil)");

		// pass reference
		_test("(fun1 &x)");
		_test("x", "11");
	}

	@Test
	void test_fun_overload_arg_type() {

		_setup();
		_test("(defun fun1 ((?v1 int) ?v2) \"a fun description\" (+ ?v1 ?v2))", "'(fun1 INTEGER nil)");
		_test("(fun1 2 3)", "5");
		_test("(fun1 2 3.1)", "5.1");
		_test_error("(fun1 2.1 3.1)", "the type<FLOAT> of 0 argument<2.1> not match <INTEGER>");

		_setup();
		_test("(defun fun1 ((?v1 int) ?v2) \"a fun description\" (+ ?v1 ?v2))", "'(fun1 INTEGER nil)");
		_test("(defun fun1 (?v1 ?v2) \"a fun description\" (- ?v1 ?v2))", "'(fun1 nil nil)");
		_test("(fun1 2 3)", "5");
		_test("(fun1 2 3.1)", "5.1");
		_test("(fun1 2.1 3.1)", "-1.0");
	}

	@Test
	void test_fun_overload_arg_number() {

		_setup();

		_test("(defun fun (?v) (+ ?v 1))", "'(fun nil)");
		_test("(fun 1)", "2");

		_test("(defun fun (?v1 ?v2) (+ ?v1 ?v2))", "'(fun nil nil)");
		_test("(fun 1 2)", "3");
	}
}
