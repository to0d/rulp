package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorStrMatchTest extends RulpTestBase {

	@Test
	public void test_str_match_1() {

		_setup();
		_test_script("test/beta/rulp/factor/test_str_match_1.rulp");
	}

	@Test
	public void test_str_match_2() {

		_setup();
		_test_script("test/beta/rulp/factor/test_str_match_2.rulp");
	}
}
