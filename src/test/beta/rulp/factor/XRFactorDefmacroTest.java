package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.RulpTestBase;

class XRFactorDefmacroTest extends RulpTestBase {

	@Test
	void test() {

		_test("(defmacro ?none () ()) (?none)", "?none ()");

		_test("(defmacro fun1 (?v1 ?v2) \"a macro description\" (?v1 ?v2 3)) (fun1 + 1)", "fun1 4");

		_test("(defmacro ?query_as00_component (?team) (join ((query_tag_relation AS400 hasChild ?) (query_tag_relation ?team termAttrOf ?))))",
				"?query_as00_component");
	}

}
