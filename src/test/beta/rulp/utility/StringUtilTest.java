package beta.rulp.utility;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import alpha.rulp.utility.StringUtil;

class StringUtilTest {

	@Test
	public void test_removeEscapeString() {

		assertEquals("", StringUtil.removeEscapeString(""));
		assertEquals(null, StringUtil.removeEscapeString(null));
		assertEquals("a", StringUtil.removeEscapeString("a"));
		assertEquals(" ", StringUtil.removeEscapeString(" "));
		assertEquals(" a ", StringUtil.removeEscapeString(" a "));
		assertEquals(" a\nb ", StringUtil.removeEscapeString(" a\\" + "nb "));
		assertEquals(" a\nb ", StringUtil.removeEscapeString(" a\\nb "));
		assertEquals(" a\\b ", StringUtil.removeEscapeString(" a\\\\b "));
	}
}
