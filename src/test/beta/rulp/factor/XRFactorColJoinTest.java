package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorColJoinTest extends RulpTestBase {

	@Test
	void test() {
		_test("(join '(a b c) '(x y z))", "'()");
		_test("(join '(a b c) '(x y a))", "'(a)");
	}

}
