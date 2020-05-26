
package beta.rulp.runtime;

import org.junit.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.ximpl.runtime.XTokener;
import alpha.rulp.ximpl.runtime.XTokener.Token;
import junit.framework.TestCase;

public class XTokenerTest extends TestCase {

	public static void _test_char_type(String input, String expect) {

		String output = "";
		for (int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			output += XTokener.getCharType(c);
		}

		assertEquals(expect, output);
	}

	public static void _test_token_parse(String input, String expect) {

		XTokener parser = new XTokener(input);
		StringBuilder sb = new StringBuilder();

		Token token = null;
		try {

			while ((token = parser.next()) != null) {
				sb.append(token.toString());
				sb.append("; ");
			}

		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		sb.setLength(sb.length() - 1);
		assertEquals(expect, sb.toString());
	}

	@Test
	public void test_char_type() {
		_test_char_type("abc123\'\"  %?\n\r", "00011163226677");
		_test_char_type("哈利·托特达夫", "0060000");
	}

	@Test
	public void test_token_parse() {

		_test_token_parse("哈利·托特达夫", "[N:2:0:2:哈利]; [S:1:0:3:·]; [N:4:0:7:托特达夫];");

		// _testTokenParser("a\nb", "[S:1:1:<];");

		_test_token_parse("<", "[S:1:0:1:<];");

		_test_token_parse("a-b", "[N:1:0:1:a]; [S:1:0:2:-]; [N:1:0:3:b];");

		_test_token_parse("ABC123", "[N:6:0:6:ABC123];");

		_test_token_parse("123ABC", "[N:6:0:6:123ABC];");

		_test_token_parse("ABC 123", "[N:3:0:3:ABC]; [X:1:0:4: ]; [I:3:0:7:123];");

		_test_token_parse("ABC -123", "[N:3:0:3:ABC]; [X:1:0:4: ]; [I:4:0:8:-123];");

		_test_token_parse("ABC -123.5", "[N:3:0:3:ABC]; [X:1:0:4: ]; [F:6:0:10:-123.5];");

		_test_token_parse("123 ABC", "[I:3:0:3:123]; [X:1:0:4: ]; [N:3:0:7:ABC];");

		_test_token_parse("123_ABC", "[N:7:0:7:123_ABC];");

		_test_token_parse("ABC_123", "[N:7:0:7:ABC_123];");

		_test_token_parse("ABC_123___", "[N:10:0:10:ABC_123___];");

		_test_token_parse("_ABC123", "[N:7:0:7:_ABC123];");

		_test_token_parse("____ABC123", "[N:10:0:10:____ABC123];");

		_test_token_parse("____ABC123___", "[N:13:0:13:____ABC123___];");

		_test_token_parse("__", "[N:2:0:2:__];");

		_test_token_parse("_123ABC", "[N:7:0:7:_123ABC];");

		_test_token_parse("XXX 123ABC", "[N:3:0:3:XXX]; [X:1:0:4: ]; [N:6:0:10:123ABC];");

		_test_token_parse("XXX 123 ABC", "[N:3:0:3:XXX]; [X:1:0:4: ]; [I:3:0:7:123]; [X:1:0:8: ]; [N:3:0:11:ABC];");

		_test_token_parse("XXX 123.5 ABC",
				"[N:3:0:3:XXX]; [X:1:0:4: ]; [F:5:0:9:123.5]; [X:1:0:10: ]; [N:3:0:13:ABC];");

		_test_token_parse(".2 A", "[S:1:0:1:.]; [I:1:0:2:2]; [X:1:0:3: ]; [N:1:0:4:A];");

		_test_token_parse("ABC \" ' 12\" ", "[N:3:0:3:ABC]; [X:1:0:4: ]; [T:7:0:11:\" ' 12\"]; [X:1:0:12: ];");

		_test_token_parse("{}", "[S:1:0:1:{]; [S:1:0:2:}];");

		_test_token_parse("{};", "[S:1:0:1:{]; [S:1:0:2:}]; [S:1:0:3:;];");

	}

}
