package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorToStringTest extends RulpTestBase {

	@Test
	void test() {
		_test("(to-string 123)", "\"123\"");
	}
}
