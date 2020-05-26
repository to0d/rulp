package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorGetTest extends RulpTestBase {

	@Test
	void test() {
		_test("(get '(a b c) 1)", "b");
	}
}
