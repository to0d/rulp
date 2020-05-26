/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import alpha.rulp.lang.RException;
import alpha.rulp.utility.StringUtil;

public class XTokener {

	public static class Token {

		public static final int TT_0BAD = 0;
		public static final int TT_1BLK = 1; // symbol blank space
		public static final int TT_2SYM = 2; // other symbol
		public static final int TT_3NAM = 3; // simple string, ABC123, _123ABC,
		public static final int TT_4STR = 4; // in quotation marks, "abc123 "
		public static final int TT_5INT = 5; // integer, 0123
		public static final int TT_6FLT = 6; // float 123.5
		public static final int TT_7CBI = 7; // Combine Symbols "%%"
		public static final int TT_8FLE = 8; // float 11.0e+4, scientific expression
		public static final int TT_9END = 9; // '\n'

		public static char getType(int t) {
			switch (t) {
			case TT_0BAD:
				return 'B';

			case TT_1BLK:
				return 'X';

			case TT_2SYM:
				return 'S';

			case TT_7CBI:
				return 'C';

			case TT_3NAM:
				return 'N';

			case TT_4STR:
				return 'T';

			case TT_5INT:
				return 'I';

			case TT_6FLT:
				return 'F';

			default:
				return 'U';
			}
		}

		public final int endPos;

		public int lineIndex = 0;

		public final int type;

		public final String value;

		public Token(int tokenType, String tokenValue, int tokenEndPos) {
			super();
			this.type = tokenType;
			this.value = tokenValue;
			this.endPos = tokenEndPos;
		}

		@Override
		public String toString() {
			return "[" + getType(type) + ":" + (value == null ? 0 : value.length()) + ":" + lineIndex + ":" + endPos
					+ ":" + value + "]";
		}
	}

	final static String CombinedSymbols[] = { "%%" };

	static final int CT_BLANK = 2; // Symbol Blank " " or \t

	static final int CT_CHAR = 0; // character

	static final int CT_END = 7; // New line \r \n

	static final int CT_NUM = 1; // number

	static final int CT_PERIOD = 5; // Symbol .

	static final int CT_SINGLE = 6; // SingleSymbol

	static final int CT_STRING = 3; // String ""

	static final int CT_UNDERSCORE = 4; // Symbol _

	static final int CT_UNKNOWN = 8; // unknown char

	static final int MaxCombineSymbolLength;

	static final int SS_0INI = 0; // Init mode

	static final int SS_1SKI = 1; // Skip mode

	static final int SS_2BLK = 2; // Blank mode

	static final int SS_3NAM = 3; // Name mode

	static final int SS_4INT = 4; // Integer mode

	static final int SS_5FLO = 5; // Float mode

	static final int SS_6STR = 6; // String mode

	static final int SS_BAD_ = -99; // bad mode

	static final int SS_OUT1 = -1; // output Name (without curChar)

	static final int SS_OUT2 = -2; // output integer (without curChar)

	static final int SS_OUT3 = -3; // output float (without curChar)

	static final int SS_OUT4 = -4; // output Blank (without curChar)

	static final int SS_OUT5 = -5; // output Symbol (with curChar)

	static final int SS_OUT6 = -8; // output String with curChar

	static final int SS_SSTATE[][] = {

			// -CHAR---NUMBER---BLANK----" " ' "---" _ "----" . "----SYMBOL----END
			{ SS_3NAM, SS_4INT, SS_2BLK, SS_6STR, SS_3NAM, SS_OUT5, SS_OUT5, SS_1SKI }, // SS_0INI
			{ SS_3NAM, SS_4INT, SS_2BLK, SS_6STR, SS_3NAM, SS_OUT5, SS_OUT5, SS_1SKI }, // SS_1SKI
			{ SS_OUT4, SS_OUT4, SS_2BLK, SS_OUT4, SS_OUT4, SS_OUT4, SS_OUT4, SS_OUT4 }, // SS_2BLK
			{ SS_3NAM, SS_3NAM, SS_OUT1, SS_OUT1, SS_3NAM, SS_OUT1, SS_OUT1, SS_OUT1 }, // SS_3NAM
			{ SS_3NAM, SS_4INT, SS_OUT2, SS_OUT2, SS_3NAM, SS_5FLO, SS_OUT2, SS_OUT2 }, // SS_4INT
			{ SS_BAD_, SS_5FLO, SS_OUT3, SS_OUT3, SS_BAD_, SS_BAD_, SS_OUT3, SS_OUT3 }, // SS_5FLO, float
			{ SS_6STR, SS_6STR, SS_6STR, SS_OUT6, SS_6STR, SS_6STR, SS_6STR, SS_BAD_ }, // SS_6STR
	};

	static {

		int len = -1;
		for (int i = 0; i < CombinedSymbols.length; ++i) {
			String cs = CombinedSymbols[i];
			if (cs.length() > len) {
				len = cs.length();
			}
		}

		MaxCombineSymbolLength = len;
	}

	static boolean _isSingleSymbol(Token token, char c) {
		return token != null && token.type == Token.TT_2SYM && token.value.length() == 1 && token.value.charAt(0) == c;
	}

	public static final char EN_SEPARATION_DOT = 0xb7; // '¡¤'

	public static int getCharType(char c) {

		switch (c) {
		case 0x0A: // '\n';
		case 0x0D: // '\r'
			return CT_END;
		case 0x20: // blank
		case 0x09: // tab
			return CT_BLANK;
		case '"':
			return CT_STRING;
		case '\'':
		case ':':
		case ';':
		case ',':
		case '[':
		case ']':
		case '{':
		case '}':
		case '(':
		case ')':
		case '@':
		case '#':
		case '=':
		case '/':
		case '$':
		case '+':
		case '-':
		case '?':
		case '&':
		case '*':
		case '%':
		case '<':
		case '>':
		case '!':
		case '^':
		case EN_SEPARATION_DOT:
			return CT_SINGLE;
		case '_':
			return CT_UNDERSCORE;
		case '.':
			return CT_PERIOD;

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return CT_NUM;

		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return CT_CHAR;

		default:

			if (StringUtil.isChinese(c) || StringUtil.isChineseSymbol(c)) {
				return CT_CHAR;
			}

			return CT_UNKNOWN;
		}
	}

	private String content;

	protected int curPos;

	private int length;

	public XTokener(String content) {

		this.content = (content == null ? "" : content);
		this.length = content.length();
		this.curPos = 0;
	}

	public Token next() throws RException {

		Token token = peek();
		curPos = (token == null ? length : token.endPos);
		return token;
	}

	public Token peek() throws RException {

		Token token = null;
		Token nextToken = null;

		if ((token = scan(curPos)) == null) {
			return null;
		}

		// -123 or -1.5
		if (_isSingleSymbol(token, '-') && (nextToken = scan(token.endPos)) != null
				&& (token.endPos + nextToken.value.length()) == nextToken.endPos
				&& (nextToken.type == Token.TT_6FLT || nextToken.type == Token.TT_5INT)) {

			return new Token(nextToken.type, token.value + nextToken.value, nextToken.endPos);
		}

		// Combined Symbols "%%"
		if (token.type == Token.TT_2SYM && token.value.length() == 1) {

			String hittingSymbols[] = new String[CombinedSymbols.length];
			for (int i = 0; i < CombinedSymbols.length; ++i)
				hittingSymbols[i] = CombinedSymbols[i];
			int hittingSymbolCount = CombinedSymbols.length;

			StringBuilder sb = new StringBuilder();
			int count = 0;

			nextToken = token;
			int lastTokenEndPos = token.endPos - 1;

			while (count < MaxCombineSymbolLength && hittingSymbolCount > 0 && nextToken != null
					&& nextToken.type == Token.TT_2SYM && nextToken.value.length() == 1
					// need continue
					&& (lastTokenEndPos + 1) == nextToken.endPos) {

				char c = nextToken.value.charAt(0);
				int hitCount = 0;
				int lastEmptySlot = -1;

				for (int i = 0; i < hittingSymbolCount; ++i) {

					String hittingSymbol = hittingSymbols[i];

					// hit char
					if (count < hittingSymbol.length() && hittingSymbol.charAt(count) == c) {

						if (lastEmptySlot != -1) {
							hittingSymbols[lastEmptySlot++] = hittingSymbol;
						}

						++hitCount;

					} else {
						if (lastEmptySlot == -1) {
							lastEmptySlot = i;
						}
					}
				}

				hittingSymbolCount = hitCount;
				if (hitCount == 0) {
					break; // break while
				}

				sb.append(c);
				lastTokenEndPos = nextToken.endPos;
				nextToken = scan(nextToken.endPos);
				++count;
			}

			if (count > 0 && sb.length() > 0) {

				String symbol = sb.toString();
				for (int i = 0; i < CombinedSymbols.length; ++i) {
					String cs = CombinedSymbols[i];
					if (symbol.equals(cs)) {
						return new Token(Token.TT_7CBI, symbol, lastTokenEndPos);
					}
				}
			}
		}

		return token;
	}

	private Token scan(int begPos) throws RException {

		int scanPos = begPos;
		int retPos = -1;
		int lastState = SS_0INI;
		int curState = SS_0INI;
		int findTokenType = -1;

		char lastChar = 0;
		char stringBeginSymbol = 0;

		if (begPos >= length) {
			return null;
		}

		for (; findTokenType == -1 && scanPos < length; lastState = curState, scanPos++) {
			lastChar = content.charAt(scanPos);
			int charType = getCharType(lastChar);
			if (charType == CT_UNKNOWN) {

				// skip some char in a string " abc??def "
				if (lastState == SS_6STR) {
					continue;
				}

				// unsupport char
				throw new RException(content + ":unsupport char<" + lastChar + ">, pos=" + scanPos);
			}

			curState = SS_SSTATE[lastState][charType];

			switch (curState) {
			case SS_BAD_:

				// example 11.0e+4
				if (lastState == SS_5FLO && lastChar == 'e') {

					// TT_8FLE
				}

				throw new RException(content + ": Invald DFA state in char<" + lastChar + ">, pos=" + scanPos);

			case SS_1SKI:
				++begPos;
				break;

			// output Token-Name without curChar
			case SS_OUT1:

				retPos = scanPos;
				findTokenType = Token.TT_3NAM;
				break;

			// output Token-integer( without curChar)
			case SS_OUT2:

				retPos = scanPos;
				findTokenType = Token.TT_5INT;
				break;

			// output Token-float( without curChar)
			case SS_OUT3:
				retPos = scanPos;
				findTokenType = Token.TT_6FLT;
				break;

			// output Token-Blank( without curChar)
			case SS_OUT4:

				retPos = scanPos;
				findTokenType = Token.TT_1BLK;
				break;

			// output Single-Symbol (with curChar)
			case SS_OUT5:

				retPos = scanPos + 1;
				if (retPos != (begPos + 1))
					throw new RException(content + ": Invald DFA state in char<" + lastChar + ">, pos=" + scanPos);

				findTokenType = Token.TT_2SYM;
				break;

			// output Token-String with curChar
			case SS_OUT6:

				if (stringBeginSymbol == 0)
					throw new RException(content + ": Null String Char, pos=" + scanPos);

				// Start with " and new the lastChar is '
				if (stringBeginSymbol != lastChar) {

					// Continue scan to "StringBeginSymbol"
					curState = SS_6STR;

				} else {

					retPos = scanPos + 1;
					findTokenType = Token.TT_4STR;
				}

				break;

			case SS_6STR:

				if (stringBeginSymbol == 0) {
					stringBeginSymbol = lastChar;
				}

				break;

			}// switch (curState)
		}

		if (findTokenType == -1) {

			switch (curState) {

			case SS_1SKI:
				// String end in \n or \r
				return null;

			case SS_3NAM:
				findTokenType = Token.TT_3NAM;
				break;
			case SS_4INT:
				findTokenType = Token.TT_5INT;
				break;
			case SS_5FLO:
				findTokenType = Token.TT_6FLT;
				break;
			case SS_2BLK:
				findTokenType = Token.TT_1BLK;
				break;
			default:
				findTokenType = Token.TT_0BAD;
			}

			retPos = length;
		}

		if (begPos >= retPos)
			throw new RException(content + ": invaild length, <" + begPos + ":" + retPos + ">, pos=" + scanPos);

		return new Token(findTokenType, content.substring(begPos, retPos), retPos);
	}

}
