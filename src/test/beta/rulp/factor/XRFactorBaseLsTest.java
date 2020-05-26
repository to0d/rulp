package beta.rulp.factor;

import org.junit.Test;

import alpha.rulp.utility.RulpTestBase;

public class XRFactorBaseLsTest extends RulpTestBase {

	@Test
	public void test() {

		_setup();

		_test("(ls *)", "'()");
		_test("(ls 0)", "'()");
		_test("(ls)", "'()");

		_setup();
		_test("(ls-print sys)", "nil", _load("test/beta/rulp/factor/base_ls.txt") + "\n");

	}

	@Test
	public void test_list_env_vars() {

		_setup();
		_test("(let ($idx 1) (loop for ?x in (ls env) do (if (not (str-equal (type-of ?x) \"VAR\")) (continue)) (print $idx \":\"  (type-of ?x) \", name=\" (name-of ?x) \"\\n\") (setq $idx (+ $idx 1)) ))",
				"nil", _load("test/beta/rulp/factor/base_ls_var.txt") + "\n");
	}

	@Test
	public void test_list_usr_vars() {

		_setup();
		_test("(defvar x 10)", "&x");
		_test("(defvar y)", "&y");
		_test("(ls 0)", "'(&x &y)");
		_test("(ls-print *)", "nil", "1:VAR, name=$idx\n" + "2:VAR, name=x\n" + "3:VAR, name=y\n");
	}
}
