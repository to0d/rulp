package beta.rulp.factor;

import org.junit.Test;

import alpha.rulp.utility.RulpTestBase;

public class XRFactorAliasTest extends RulpTestBase {

	@Test
	public void test() {

		_setup();
		_test("(defvar x 10) x (alias x y) y", "&x 10 &x 10");

		_setup();
		_test("(alias + myplus) (myplus 1 2)", "+ 3");
	}
}
