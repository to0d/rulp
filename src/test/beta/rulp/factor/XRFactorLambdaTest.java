package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorLambdaTest extends RulpTestBase {

	@Test
	void test() {
		_test("((lambda (?v1 ?v2) (?v1 ?v2 3)) + 1)", "4");
	}

}
