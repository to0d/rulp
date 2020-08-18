package beta.rulp.factor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorStrStartsWithTest extends RulpTestBase {

	@Test
	public void test_str_match_1() {

		_setup();
		_test_script("test/beta/rulp/factor/test_str_start_with_1.rulp");
	}
}
