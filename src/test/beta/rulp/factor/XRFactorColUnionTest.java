package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

public class XRFactorColUnionTest extends RulpTestBase {

	@Test
	void test() {
		_test("(union '(a b c) '(x y z))", "'(a b c x y z)");
		_test("(union '('(a b c) '(b c)))", "'(a b c)");
	}

}
