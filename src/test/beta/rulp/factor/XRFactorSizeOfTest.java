package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorSizeOfTest extends RulpTestBase {

	@Test
	void test() {
		_test("(size-of '(a b c))", "3");
	}
}
