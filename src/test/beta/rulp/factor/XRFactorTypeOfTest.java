package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorTypeOfTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(type-of nil)", "NULL");
		_test("(type-of true)", "BOOL");
		_test("(type-of 1)", "INTEGER");
		_test("(type-of 1.1)", "FLOAT");
		_test("(type-of '(a b))", "LIST");
		_test("(type-of print-list)", "MACRO");
		_test("(type-of \"abc\")", "STRING");
		_test("(defvar var 10)(type-of var)", "&var VAR");
	}

}
