package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorErrorTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(try (if (> 2 1) (error e1 \"msg1\") (error e2 \"msg2\")) (e1 (print (get-err-value e1)) (return 1)) (e2 (return 2)) )",
				"1", "msg1");
	}

	@Test
	void test_default_handle_case() {

		_setup();
		_test("(try (if (> 2 3) (error e1 \"msg1\") (error e2 \"msg2\")) (?e (print (get-err-value ?e)) (return ?e)))",
				"error#e2:msg2", "msg2");
	}

	@Test
	void test_unhandle_case_1() {

		_setup();
		_test("(try (try (error e1 \"msg1\") (e2 (return 2))) (?e (return ?e)))", "error#e1:msg1");
	}

	@Test
	void test_unhandle_case_2() {

		_setup();
		_test_error("(try (error e1 \"msg1\") (e2 (return 2)))",
				"Unhandled error: id=\"error#e1:msg1\", from=\"error\"");
	}
}
