package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorValueTypeOfTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(defvar var 10)(value-type-of var)", "&var INTEGER");
	}

}
