package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorPrintListTest extends RulpTestBase {

	@Test
	void test() {
		_test("(print-list '(a b c))", "'(a b c)", "a\nb\nc\n");
	}

}
