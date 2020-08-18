/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.A_FALSE;
import static alpha.rulp.lang.Constant.A_TRUE;
import static alpha.rulp.lang.Constant.O_False;
import static alpha.rulp.lang.Constant.O_True;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.error.RParseException;
import alpha.rulp.lang.error.RulpIncompleteException;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.runtime.IRTokener;
import alpha.rulp.runtime.IRTokener.Token;
import alpha.rulp.runtime.IRTokener.TokenType;
import alpha.rulp.runtime.RName;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.StringUtil;

/* ************************************************************ */
// RULP rules                                 
//                                                  
//    Lists      := List Lists 
//			      | {empty}
//
//    List       := (Expressions) 
//
//	  Expressions:= Object Expressions
//                | {empty}
//          
//
//	  Object     := Atom
//			      | String
//			      | List
//
// 	  Atom       := [A-Za-z_$]{A-Za-z,0-9,-,_+&}  
//
//    String     := "any string"         
//                                                                      
//                                     
// Examples
//    (a b c)
/* ************************************************************ */
public class XRParser implements IRParser {

	static final Token END_TOKEN = new Token(TokenType.TT_9END, null, -1);

	public static final int MAX_PARSE_COUNT = 65535 * 64;

	public static final int MAX_STACK_DEPTH = 65535;

	public static final int MAX_TOKEN_COUNT = 65535 * 64;

	private static boolean _isBlankToken(Token token) {
		return token.type == TokenType.TT_1BLK;
	}

	private static boolean _isEndToken(Token token) {
		return token.type == TokenType.TT_9END;
	}

	static boolean _isSeparatorToken(Token token) throws RException {

		if (token == null) {
			return true;
		}

		switch (token.type) {
		case TT_1BLK:
		case TT_9END:
			return true;

		case TT_2SYM:
			switch (token.value) {
			case "(":
			case ")":
				return true;
			}

		default:

		}

		return false;
	}

	private static boolean _isSupportIdentifierHeadToken(Token token) {

		if (token == null || token.value == null || token.value.length() == 0) {
			return false;
		}

		switch (token.type) {

		case TT_3NAM: {

			switch (XRTokener.getCharType(token.value.charAt(0))) {
			case XRTokener.C00_CHAR:
			case XRTokener.C01_NUM:
			case XRTokener.C04_UNDERSCORE:
				return true;
			}

			break;
		}

		case TT_2SYM:
			switch (token.value) {
			case "$":
			case "_":
			case "?":
				return true;
			}

			break;

		default:
			return false;
		}

		return false;
	}

	static boolean _isSymbolToken(Token token, char symbol) throws RException {

		if (token != null && token.type == TokenType.TT_2SYM && token.value != null && token.value.length() == 1
				&& token.value.charAt(0) == symbol) {

			return true;
		}

		return false;
	}

	static <T> void _set(ArrayList<T> array, int index, T v) {

		if (index < array.size()) {
			array.set(index, v);

		} else {

			while (index > array.size()) {
				array.add(null);
			}

			array.add(v);
		}
	}

	private int lineIndex = 0;

	private int linePos = 0;

	private ArrayList<String> lines = new ArrayList<>();

	private int operationCount = 0;

	private Map<String, String> prefixNameSpaceMap = new HashMap<>();

	private int stackDepth = 0;

	private boolean supportComment = true;

	private boolean supportNumber = true;

	private int tokenCount = 0;

	private IRTokener tokener;

	private ArrayList<Integer> tokenIndexs = new ArrayList<>();

	private ArrayList<Token> tokenList = new ArrayList<>();

	public XRParser(IRTokener tokener) {
		super();
		this.tokener = tokener;
	}

	private void _checkRecursion() throws RException {

		if (operationCount++ >= MAX_PARSE_COUNT) {
			throw new RException();
		}
	}

	private Token _curToken() throws RException {

		if (_tokenPos() >= tokenCount)
			return null;

		Token token = tokenList.get(_tokenPos());
		linePos = token.endPos;
		return token;
	}

	private int _depth() throws RException {

		if (stackDepth >= MAX_STACK_DEPTH) {
			throw new RException(String.format("[%d, %d]: out of Stack", lineIndex, linePos));
		}

		return stackDepth;
	}

	private RName _getRName(String name) {

		int pos = name.lastIndexOf(':');
		if (pos == -1) {
			return null;
		}

		String prefix = name.substring(0, pos);
		String nameSpace = prefixNameSpaceMap.get(prefix);
		if (nameSpace == null) {
			return null;
		}

		String subName = name.substring(pos + 1);
		return new RName(nameSpace, prefix, subName, nameSpace + subName);
	}

	private boolean _ignoreBlank() throws RException {

		_checkRecursion();

		boolean ignoreComment = false;

		NEXT: while (_more()) {

			Token token = _curToken();

			if (ignoreComment) {

				// ignore end tokens
				if (_isEndToken(token)) {
					this.lineIndex++;
					ignoreComment = false;
				}

				_pushStack(1);
				continue NEXT;

			} else {

				// ignore blank tokens
				if (_isBlankToken(token)) {
					_pushStack(1);
					continue NEXT;
				}

				// ignore end tokens
				if (_isEndToken(token)) {
					this.lineIndex++;
					_pushStack(1);
					continue NEXT;
				}

				if (isSupportComment()) {

					// find symbol ";"
					if (token.type == TokenType.TT_2SYM && token.value != null && token.value.equals(";")) {
						_pushStack(1);
						ignoreComment = true;
						continue NEXT;
					}
				}
			}

			break;
		}

		return true;
	}

	private void _init() {

		this.operationCount = 0;
		this.stackDepth = 0;
		this.tokenCount = 0;
		this.lineIndex = 0;
		this.linePos = 0;

		this.lines.clear();
		this.tokenList.clear();
		this.tokenIndexs.clear();
		this.tokenIndexs.add(0);
	}

	private boolean _more() throws RException {
		return _tokenPos() < tokenCount;
	}

	private void _pullStack(int newDepth) throws RException {

		if (newDepth < 0 || newDepth > _depth())
			throw new RException();

		this.stackDepth = newDepth;
	}

	private void _pushStack(int addTokenCount) throws RException {

		if (stackDepth >= (MAX_STACK_DEPTH - 1) || addTokenCount < 0)
			throw new RException();

		int pos = _tokenPos() + addTokenCount;
		if (pos >= MAX_TOKEN_COUNT) {
			throw new RException(String.format("[%d, %d]: buffer overflow", lineIndex, linePos));
		}

		_set(tokenIndexs, ++stackDepth, pos);
	}

	private int _tokenPos() throws RException {
		return tokenIndexs.get(_depth());
	}

	@Override
	public IRTokener getTokener() {
		return tokener;
	}

	public boolean isSupportComment() {
		return supportComment;
	}

	public boolean isSupportNumber() {
		return supportNumber;
	}

	private IRExpr matchExpression() throws RException {

		_checkRecursion();

		if (!_more()) {
			return null;
		}

		// save depth of option
		int depth = _depth();

		if (!matchSymbol('(')) {
			_pullStack(depth);
			return null;
		}

		_ignoreBlank();

		/* save depth of option */
		int depth2 = _depth();

		if (matchSymbol(')')) {
			return RulpFactory.createExpression();
		} else {
			_pullStack(depth2);
		}

		ArrayList<IRObject> list = new ArrayList<>();
		IRObject obj = null;
		while (_ignoreBlank() && (obj = nextObject()) != null) {
			list.add(obj);
		}

		if (_ignoreBlank() && matchSymbol(')')) {
			return RulpFactory.createExpression(list);
		}

		_pullStack(depth);
		return null;
	}

	private IRObject matchFloat() throws RException {

		_checkRecursion();

		int depth = _depth();

		Token pToken;

		if ((pToken = _curToken()) != null && pToken.type == TokenType.TT_6FLT) {

			_pushStack(1);

			if (this.isSupportNumber()) {
				return (RulpFactory.createFloat(Float.valueOf(pToken.value)));
			} else {
				return (RulpFactory.createAtom(pToken.value));
			}

		}

		_pullStack(depth);
		return null;
	}

	private IRObject matchInteger() throws RException {

		_checkRecursion();

		int depth = _depth();

		Token pToken;

		if ((pToken = _curToken()) != null && pToken.type == TokenType.TT_5INT) {
			_pushStack(1);
			if (this.isSupportNumber()) {
				return (RulpFactory.createInteger(Integer.valueOf(pToken.value)));
			} else {
				return (RulpFactory.createAtom(pToken.value));
			}

		}

		_pullStack(depth);
		return null;
	}

	private IRList matchList() throws RException {

		_checkRecursion();

		if (!_more()) {
			return null;
		}

		// save depth of option
		int depth = _depth();

		if (!matchSymbol('(')) {
			_pullStack(depth);
			return null;
		}

		_ignoreBlank();

		/* save depth of option */
		int depth2 = _depth();

		if (matchSymbol(')')) {
			return RulpFactory.createList();
		} else {
			_pullStack(depth2);
		}

		ArrayList<IRObject> list = new ArrayList<>();
		IRObject obj = null;
		while (_ignoreBlank() && (obj = nextObject()) != null) {
			list.add(obj);
		}

		if (_ignoreBlank() && matchSymbol(')')) {
			return RulpFactory.createList(list);
		}

		_pullStack(depth);
		return null;
	}

	private IRAtom matchOperator() throws RException {

		_checkRecursion();

		int depth = _depth();

		Token token;

		String sym = "";

		NEXT_TOKEN: while (_more() && (token = _curToken()) != null && token.type == TokenType.TT_2SYM) {

			switch (token.value) {
			case "(":
			case ")":
				_pullStack(depth);
				break NEXT_TOKEN;

			default:
				sym += token.value;
				_pushStack(1);
				depth = _depth();
			}
		}

		if (!sym.isEmpty()) {
			return RulpFactory.createAtom(sym);
		}

		_pullStack(depth);
		return null;
	}

	private IRString matchString() throws RException {

		_checkRecursion();

		int depth = _depth();

		Token pToken;

		if ((pToken = _curToken()) != null && pToken.type == TokenType.TT_4STR) {

			String value = pToken.value;
			value = value.substring(1, value.length() - 1);
			_pushStack(1);

			return RulpFactory.createString(value);
		}

		_pullStack(depth);
		return null;
	}

	private boolean matchSymbol(char symbol) throws RException {

		_checkRecursion();

		if (_isSymbolToken(_curToken(), symbol)) {

			/* output symbol */
			_pushStack(1);
			return true;
		}

		return false;
	}

	private String nextAtom() throws RException {

		_checkRecursion();

		/* save depth of option */
		int depth = _depth();
		Token token = null;

		if ((token = _curToken()) != null && _isSupportIdentifierHeadToken(token)) {

			_pushStack(1);

			String atomName = token.value;
			FIND: while (_more() && (token = _curToken()) != null && token.value != null && token.value.length() > 0
					&& !_isBlankToken(token)) {

				if (token.type == TokenType.TT_2SYM) {
					switch (token.value) {
					case "(":
					case ")":
						break FIND;
					}
				}

				atomName += token.value;
				_pushStack(1);

			}

			return atomName;
		}

		_pullStack(depth);
		return null;
	}

	private IRObject nextObject() throws RException {

		_checkRecursion();

		if (!_more()) {
			return null;
		}

		int depth = _depth();

		Token token = _curToken();
		_pushStack(1);
		Token next = _curToken();

		/******************************************/
		// Combine Symbols
		/******************************************/
		if (!_isSeparatorToken(next)) {

			switch (token.type) {

			case TT_2SYM:

				switch (token.value) {

				case "&":
					/******************************************/
					// Try match var: &abc
					/******************************************/
					String atomName = nextAtom();
					if (atomName != null) {
						return RulpFactory.createVar(atomName);
					}
					break;

				case "+":
				case "-":

					/******************************************/
					// Try match (+/-)number
					/******************************************/
					if (this.isSupportNumber()) {

						_pushStack(1);

						if (!_isSeparatorToken(next)) {

							switch (next.type) {

							// -123 or +123
							case TT_5INT:
								int intVal = Integer.valueOf(next.value);
								if (token.value.equals("-")) {
									intVal = -intVal;
								}

								return RulpFactory.createInteger(intVal);

							// -1.5 or +1.5
							case TT_6FLT:

								float fltVal = Float.valueOf(next.value);
								if (token.value.equals("-")) {
									fltVal = -fltVal;
								}

								return RulpFactory.createFloat(fltVal);

							default:
							}
						}

					}

					break; // break switch cur_token value

				default: // other symbol

				} // end of switch cur_token value
				break;

			default:

			} // switch cur_token type

		}
		// non Combine Symbols
		else {

			switch (token.type) {
			/******************************************/
			// Try match integer
			/******************************************/
			case TT_5INT:

				if (this.isSupportNumber()) {
					return RulpFactory.createInteger(Integer.valueOf(token.value));
				}

				break;

			/******************************************/
			// Try match float
			/******************************************/
			case TT_6FLT:

				if (this.isSupportNumber()) {
					return RulpFactory.createFloat(Float.valueOf(token.value));
				}

				break;

			/******************************************/
			// Try match string: "abc"
			/******************************************/
			case TT_4STR:
				String value = token.value;
				value = value.substring(1, value.length() - 1);
				return RulpFactory.createString(value);

			default:
			}
		}

		/******************************************/
		// Try match List: '()
		/******************************************/
		_pullStack(depth);
		if (matchSymbol('\'') && _ignoreBlank()) {
			IRList list = matchList();
			if (list != null) {
				return list;
			}
		}

		/******************************************/
		// Try match Expression: ()
		/******************************************/
		_pullStack(depth);
		{
			IRExpr expr = matchExpression();
			if (expr != null) {
				return expr;
			}
		}

		/******************************************/
		// Try match atom: abc
		/******************************************/
		_pullStack(depth);
		{
			String atomName = nextAtom();

			if (atomName != null) {

				switch (atomName) {
				case A_TRUE:
					return O_True;

				case A_FALSE:
					return O_False;

				default:

					RName rName = _getRName(atomName);
					if (rName == null) {
						return RulpFactory.createAtom(atomName);
					} else {
						return RulpFactory.createAtom(rName);
					}
				}

			}
		}

//		_pullStack(depth);
//		{
//			IRString val = matchString();
//			if (val != null) {
//				return val;
//			}
//		}

//		/******************************************/
//		// Try match integer
//		/******************************************/
//		_pullStack(depth);
//		{
//			IRObject obj = matchInteger();
//			if (obj != null) {
//				return obj;
//			}
//		}
//
//		/******************************************/
//		// Try match float
//		/******************************************/
//		_pullStack(depth);
//		{
//			IRObject obj = matchFloat();
//			if (obj != null) {
//				return obj;
//			}
//		}

		/******************************************/
		// Try match operator
		/******************************************/
		_pullStack(depth);
		String sym = "";
		while (_more() && (token = _curToken()) != null && !_isSeparatorToken(token)) {

			_pushStack(1);
			sym += token.value;
		}

		if (!sym.isEmpty()) {
			return RulpFactory.createAtom(sym);
		}

//		{
//			IRAtom val = matchOperator();
//			if (val != null) {
//				return val;
//			}
//		}

		_pullStack(depth);
		return null;
	}

	@Override
	public List<IRObject> parse(String inputLine) throws RException {

		/****************************************************/
		// Main routine
		/****************************************************/
		if (inputLine == null) {
			throw new RException("Empty Input");
		}

		this._init();

		/****************************************************/
		// Scan all tokens
		/****************************************************/
		int parseLineindex = 0;
		for (String newLine : StringUtil.splitStringByChar(inputLine, '\n')) {

			if (newLine.trim().isEmpty()) {
				continue;
			}

			tokener.setContent(newLine);
			Token token = null;
			boolean ignoreHeadSpace = true;

			while ((token = tokener.next()) != null) {

				token.lineIndex = parseLineindex;

				if (token.type == TokenType.TT_0BAD) {
					throw new RException(String.format("Bad token: %s", token.toString()));
				}

				/********************************/
				// Skip space in the head
				/********************************/
				{
					if (ignoreHeadSpace && _isBlankToken(token)) {
						continue;
					}
					ignoreHeadSpace = false;
				}

				if (tokenCount >= MAX_TOKEN_COUNT) {
					throw new RException(String.format("Too many token at line: %d", parseLineindex));
				}

				_set(tokenList, tokenCount++, token);
			}

			/****************************************************/
			// Remove tail space
			/****************************************************/
			while (tokenCount > 0 && _isBlankToken(tokenList.get(tokenCount - 1))) {
				--tokenCount;
			}

			_set(tokenList, tokenCount++, END_TOKEN);
			lines.add(newLine);
			++parseLineindex;
		}

		if (tokenCount == 0) {
			return Collections.<IRObject>emptyList();
		}

		/****************************************************/
		// Match rules
		/****************************************************/
		ArrayList<IRObject> list = new ArrayList<>();
		while (_ignoreBlank() && _more()) {

			IRObject obj = nextObject();
			if (obj == null) {

				Token lastToken = this._curToken();

				int lastLineIndex = lastToken == null ? -1 : lastToken.lineIndex;
				String lastLine = lastLineIndex == -1 ? null : lines.get(lastLineIndex);

				if (_isSymbolToken(lastToken, '(')) {
					throw new RulpIncompleteException(lastLineIndex,
							String.format("miss match '(' found in position %d, %s", lastToken.endPos - 1,
									lastLine.substring(lastToken.endPos - 1)));
				} else {
					throw new RParseException(lastLineIndex, String.format("token=%s, line=%s", lastToken, lastLine));
				}
			}

			list.add(obj);

			// Clean stack
			int curTokenPos = _tokenPos();
			this.tokenIndexs.clear();
			this.tokenIndexs.add(curTokenPos);
			this.stackDepth = 0;
		}

		return list;
	}

	@Override
	public void registerPrefix(String prefix, String nameSpace) {
		prefixNameSpaceMap.put(prefix, nameSpace);
	}

	@Override
	public void setSupportComment(boolean supportComment) {
		this.supportComment = supportComment;
	}

	@Override
	public void setSupportNumber(boolean supportNumber) {
		this.supportNumber = supportNumber;
	}

}
