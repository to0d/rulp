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
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.runtime.RName;
import alpha.rulp.utility.RulpFactory;
import alpha.rulp.utility.StringUtil;
import alpha.rulp.ximpl.runtime.XTokener.Token;

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

	static final Token END_TOKEN = new Token(Token.TT_9END, null, -1);
	public static final int MAX_PARSE_COUNT = 65535 * 64;
	public static final int MAX_STACK_DEPTH = 65535;
	public static final int MAX_TOKEN_COUNT = 65535 * 64;

	private static boolean _isBlankToken(Token token) {
		return token.type == Token.TT_1BLK;
	}

	private static boolean _isEndToken(Token token) {
		return token.type == Token.TT_9END;
	}

	private static boolean _isSupportIdentifierHeadToken(Token token) {

		if (token == null || token.value == null || token.value.length() == 0) {
			return false;
		}

		switch (token.type) {

		case Token.TT_3NAM: {
			int firstCharType = XTokener.getCharType(token.value.charAt(0));
			return firstCharType == XTokener.CT_CHAR || firstCharType == XTokener.CT_UNDERSCORE;
		}
		case Token.TT_2SYM:

			switch (token.value) {
			case "$":
			case "_":
			case "?":
				return true;
			}

			return false;
		default:
			return false;
		}

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

	private int tokenCount = 0;

	private ArrayList<Integer> tokenIndexs = new ArrayList<>();

	private ArrayList<Token> tokenList = new ArrayList<>();

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

				// find symbol ";"
				if (token.type == Token.TT_2SYM && token.value != null && token.value.equals(";")) {
					_pushStack(1);
					ignoreComment = true;
					continue NEXT;
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
		if (pos >= MAX_STACK_DEPTH) {
			throw new RException(String.format("[%d, %d]: buffer overflow", lineIndex, linePos));
		}

		_set(tokenIndexs, ++stackDepth, pos);
	}

	private int _tokenPos() throws RException {
		return tokenIndexs.get(_depth());
	}

	private boolean buildMatchExpression(List<IRObject> expression) throws RException {

		_checkRecursion();

		if (!_more()) {
			return true;
		}

		/* save depth of option */
		int depth = _depth();

		/******************************************/
		// Try match quote: '()
		/******************************************/
		if (matchSymbol('\'') && _ignoreBlank()) {

			IRList list = matchList();
			if (list != null) {
				expression.add(list);
				_ignoreBlank();

				buildMatchExpression(expression);
				return true;
			} else {
				_pullStack(depth);
			}
		} else {
			_pullStack(depth);
		}

		/******************************************/
		// Try match Expression: ()
		/******************************************/
		{
			IRList expr = matchExpression();
			if (expr != null) {
				expression.add(expr);
				_ignoreBlank();

				buildMatchExpression(expression);
				return true;
			} else {
				_pullStack(depth);
			}
		}

		/******************************************/
		// Try match var: &abc
		/******************************************/
		if (matchSymbol('&') && _ignoreBlank()) {

			String atomName = matchAtom();
			if (atomName != null) {
				expression.add(RulpFactory.createVar(atomName));
				_ignoreBlank();

				buildMatchExpression(expression);
				return true;
			} else {
				_pullStack(depth);
			}
		} else {
			_pullStack(depth);
		}

		/******************************************/
		// Try match atom: abc
		/******************************************/
		{
			String atomName = matchAtom();

			if (atomName != null) {

				switch (atomName) {
				case A_TRUE:
					expression.add(O_True);
					break;
				case A_FALSE:
					expression.add(O_False);
					break;
				default:

					RName rName = _getRName(atomName);
					if (rName == null) {
						expression.add(RulpFactory.createAtom(atomName));
					} else {
						expression.add(RulpFactory.createAtom(rName));
					}
				}

				_ignoreBlank();
				buildMatchExpression(expression);

				return true;

			} else {
				_pullStack(depth);
			}
		}

		/******************************************/
		// Try match string: "abc"
		/******************************************/
		IRString str = matchString();
		if (str != null) {

			expression.add(str);
			_ignoreBlank();
			buildMatchExpression(expression);
			return true;

		} else {
			_pullStack(depth);
		}

		/******************************************/
		// Try match int: integer
		/******************************************/
		IRInteger intVal = matchInteger();
		if (intVal != null) {
			expression.add(intVal);
			_ignoreBlank();
			buildMatchExpression(expression);
			return true;

		} else {
			_pullStack(depth);
		}

		/******************************************/
		// Try match float: integer
		/******************************************/
		IRFloat floatVal = matchFloat();
		if (floatVal != null) {
			expression.add(floatVal);
			_ignoreBlank();
			buildMatchExpression(expression);
			return true;

		} else {
			_pullStack(depth);
		}

		/******************************************/
		// Try match operator
		/******************************************/
		IRAtom opr = matchOperator();
		if (opr != null) {
			expression.add(opr);
			_ignoreBlank();
			buildMatchExpression(expression);
			return true;

		} else {
			_pullStack(depth);
		}

		return false;
	}

	private String matchAtom() throws RException {

		_checkRecursion();

		/* save depth of option */
		int depth = _depth();
		Token token = null;

		if ((token = _curToken()) != null && _isSupportIdentifierHeadToken(token)) {

			/* output token */
			_pushStack(1);

			String atomName = token.value;

			FIND: while (_more() && (token = _curToken()) != null && token.value != null && token.value.length() > 0
					&& !_isBlankToken(token)) {

				if (token.type == Token.TT_2SYM) {
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

	private IRList matchExpression() throws RException {

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
		if (buildMatchExpression(list) && _ignoreBlank() && matchSymbol(')')) {
			return RulpFactory.createExpression(list);
		}

		_pullStack(depth);
		return null;
	}

	private IRFloat matchFloat() throws RException {

		_checkRecursion();

		int depth = _depth();

		Token pToken;

		if ((pToken = _curToken()) != null && pToken.type == Token.TT_6FLT) {

			String value = pToken.value;

			/* output token */
			_pushStack(1);

			return RulpFactory.createFloat(Float.valueOf(value));
		}

		_pullStack(depth);
		return null;
	}

	private IRInteger matchInteger() throws RException {

		_checkRecursion();

		int depth = _depth();

		Token pToken;

		if ((pToken = _curToken()) != null && pToken.type == Token.TT_5INT) {
			String value = pToken.value;
			_pushStack(1);
			return RulpFactory.createInteger(Integer.valueOf(value));
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
		if (buildMatchExpression(list) && _ignoreBlank() && matchSymbol(')')) {
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

		FIND_NEXT: while (_more() && (token = _curToken()) != null && token.type == Token.TT_2SYM) {

			switch (token.value) {
			case "(":
			case ")":
				_pullStack(depth);
				break FIND_NEXT;
			default:
				sym += token.value;
				_pushStack(1);
				depth = _depth();
			}
		}

		if (sym.isEmpty()) {
			return null;
		}

		return RulpFactory.createAtom(sym);
	}

	private IRString matchString() throws RException {

		_checkRecursion();

		int depth = _depth();

		Token pToken;

		if ((pToken = _curToken()) != null && pToken.type == Token.TT_4STR) {

			String value = pToken.value;
			value = value.substring(1, value.length() - 1);

			/* output token */
			_pushStack(1);

			return RulpFactory.createString(value);
		}

		_pullStack(depth);
		return null;
	}

	private boolean matchSymbol(char symbol) throws RException {

		_checkRecursion();

		Token token;

		if ((token = _curToken()) != null && token.type == Token.TT_2SYM && token.value != null
				&& token.value.length() == 1 && token.value.charAt(0) == symbol) {

			/* output symbol */
			_pushStack(1);
			return true;
		}

		return false;
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

			XTokener parser = new XTokener(newLine);
			Token token = null;
			boolean ignoreHeadSpace = true;

			while ((token = parser.next()) != null) {

				token.lineIndex = parseLineindex;

				if (token.type == 0) {
					throw new RException(String.format("Bad token: %s", token.toString()));
				}

				if (token.type < -1) {
					throw new RException(String.format("Internal error: %s", token.toString()));
				}

				if (token.type == -1) {
					// scan token end,
					break; // break while
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
		while (_more()) {

			_ignoreBlank();

			if (!buildMatchExpression(list)) {

				Token lastToken = this._curToken();
				int lastLineIndex = lastToken == null ? -1 : lastToken.lineIndex;
				String lastLine = lastLineIndex == -1 ? null : lines.get(lastLineIndex);

				throw new RException(
						String.format("Bad Syntax at line %d: token=%s, line=%s", lastLineIndex, lastToken, lastLine));
			}
		}

		return list;
	}

	@Override
	public void registerPrefix(String prefix, String nameSpace) {
		prefixNameSpaceMap.put(prefix, nameSpace);
	}

}
