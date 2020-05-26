/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utility;

import static alpha.rulp.lang.Constant.A_NIL;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.T_Instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRError;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRNative;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.runtime.RName;

public class RulpUtility {

	static interface IRFormater {
		public void format(StringBuffer sb, IRObject obj) throws RException;
	}

	static class XRFormater implements IRFormater {

		@Override
		public void format(StringBuffer sb, IRObject obj) throws RException {

			if (obj == null) {
				sb.append(A_NIL);
				return;
			}

			switch (obj.getType()) {
			case NIL:
				sb.append(A_NIL);
				break;

			case ATOM:
				IRAtom atom = (IRAtom) obj;
				RName rName = atom.getRName();
				sb.append(rName == null ? atom.getName() : rName.getShorName());
				break;

			case INT:
				sb.append(((IRInteger) obj).asInteger());
				break;

			case FLOAT:
				sb.append(((IRFloat) obj).asFloat());
				break;

			case BOOL:
				sb.append(((IRBoolean) obj).asBoolean());
				break;

			case FACTOR:
				sb.append(((IRFactor) obj).getName());
				break;

			case FUNC:
				sb.append(((IRFunction) obj).getSignature());
				break;

			case MACRO:
				sb.append(((IRMacro) obj).getName());
				break;

			case STRING:
				sb.append("\"");
				sb.append(((IRString) obj).asString());
				sb.append("\"");
				break;

			case VAR:
				sb.append("&");
				sb.append(((IRVar) obj).getName());
				break;

			case INSTANCE:
				sb.append(((IRInstance) obj).asString());
				break;

			case CLASS:
				sb.append(((IRClass) obj).asString());
				break;

			case NATIVE:
				sb.append(((IRNative) obj).asString());
				break;

			default:
				throw new RException("unsupport type: " + obj.getType() + ", " + obj.toString());
			}
		}
	}

	static class XShortFormater extends XRFormater {

		private boolean bShort = false;

		private final int maxLength;

		public XShortFormater(int maxLength) {
			super();
			this.maxLength = maxLength;
		}

		@Override
		public void format(StringBuffer sb, IRObject obj) throws RException {

			if (bShort) {
				return;
			}

			if (sb.length() >= maxLength) {
				bShort = true;
				return;
			}

			super.format(sb, obj);
		}

	}

	static Map<String, String> loadLineMap = new HashMap<>();

	static IRFormater objFormater = new XRFormater();

	static IRFormater printFormater = new XRFormater() {

		@Override
		public void format(StringBuffer sb, IRObject obj) throws RException {

			if (obj == null) {
				sb.append(A_NIL);
				return;
			}

			switch (obj.getType()) {

			case STRING:
				sb.append(((IRString) obj).asString());
				break;

			default:
				super.format(sb, obj);
			}
		}
	};

	private static void _toString(StringBuffer sb, IRIterator<? extends IRObject> iterator, IRFormater formater)
			throws RException {

		int i = 0;
		while (iterator.hasNext()) {

			if (i++ != 0) {
				sb.append(' ');
			}

			_toString(sb, iterator.next(), formater);
		}
	}

	private static void _toString(StringBuffer sb, IRObject obj, IRFormater formater) throws RException {

		if (obj == null) {
			return;
		}

		switch (obj.getType()) {
		case LIST:
			sb.append("'(");
			_toString(sb, ((IRList) obj).iterator(), formater);
			sb.append(")");
			break;
		case EXPR:
			sb.append("(");
			_toString(sb, ((IRList) obj).iterator(), formater);
			sb.append(")");
			break;
		default:
			formater.format(sb, obj);
		}
	}

	public static void _toStringList(IRObject obj, List<String> list) throws RException {

		switch (obj.getType()) {
		case STRING:
			list.add(((IRString) obj).asString());
			break;

		case ATOM:
			list.add(((IRAtom) obj).getName());
			break;

		case EXPR:
		case LIST:
			IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
			while (iter.hasNext()) {
				list.addAll(toStringList(iter.next()));
			}
			break;

		default:
			throw new RException("Can't conver to string list: " + obj.toString());
		}
	}

	public static void addFrameObject(IRFrame frame, IRObject obj) throws RException {

		switch (obj.getType()) {
		case NIL:
		case ATOM:
			frame.setEntry(((IRAtom) obj).getName(), obj);
			break;

		case BOOL:
			frame.setEntry(((IRBoolean) obj).asString(), obj);
			break;

		case FACTOR:
			frame.setEntry(((IRFactor) obj).getName(), obj);
			break;

		case INSTANCE:
			frame.setEntry(((IRInstance) obj).getInstanceName(), obj);
			break;

		case VAR:
			frame.setEntry(((IRVar) obj).getName(), obj);
			break;

		case CLASS:
			frame.setEntry(((IRClass) obj).getClassAtom().getName(), obj);
			break;

		default:
			throw new RException("Invalid object: " + obj);
		}

	}

	public static IRAtom asAtom(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.ATOM) {
			throw new RException("Can't convert to atom: " + obj);
		}

		return (IRAtom) obj;
	}

	public static IRBoolean asBoolean(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.BOOL) {
			throw new RException("Can't convert to bool: " + obj);
		}

		return (IRBoolean) obj;
	}

	public static IRClass asClass(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.CLASS) {
			throw new RException("Can't convert to class: " + obj);
		}

		return (IRClass) obj;
	}

	public static IRError asError(IRObject obj) throws RException {

		if (!(obj instanceof IRError)) {
			throw new RException("Can't convert to error: " + obj);
		}

		return (IRError) obj;
	}

	public static IRExpr asExpression(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.EXPR) {
			throw new RException("Can't convert to expression: " + obj);
		}

		return (IRExpr) obj;
	}

	public static IRFactor asFactor(IRObject obj) throws RException {

		if (obj.getType() != RType.FACTOR) {
			throw new RException("Can't convert to factor: " + obj);
		}

		return (IRFactor) obj;
	}

	public static IRFunction asFunction(IRObject obj) throws RException {

		if (obj.getType() != RType.FUNC) {
			throw new RException("Can't convert to function: " + obj);
		}

		return (IRFunction) obj;
	}

	public static IRFunctionList asFunctionList(IRObject obj) throws RException {

		if (!(obj instanceof IRFunctionList)) {
			throw new RException("Can't convert to funclist: " + obj);
		}

		return (IRFunctionList) obj;
	}

	public static IRInstance asInstance(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.INSTANCE) {
			throw new RException("Can't convert to instance: " + obj);
		}

		return (IRInstance) obj;
	}

	public static IRInteger asInteger(IRObject obj) throws RException {

		if (obj.getType() != RType.INT) {
			throw new RException("Can't convert to integer: " + obj);
		}

		return (IRInteger) obj;
	}

	public static IRList asList(IRObject obj) throws RException {

		if (obj == null) {
			return RulpFactory.EMPTY_LIST;
		}

		if (obj.getType() != RType.LIST) {
			throw new RException("Can't convert to list: " + obj);
		}

		return (IRList) obj;
	}

	public static IRMacro asMacro(IRObject obj) throws RException {

		if (obj.getType() != RType.MACRO) {
			throw new RException("Can't convert to macro: " + obj);
		}

		return (IRMacro) obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T asNative(IRObject obj, Class<T> c) throws RException {

		if (obj.getType() != RType.NATIVE) {
			throw new RException("Can't convert to native: " + obj);
		}

		IRNative nativeObj = (IRNative) obj;
		Class<?> nativeClass = nativeObj.getNativeClass();

		if (!c.isAssignableFrom(nativeClass)) {
			throw new RException("Can't convert to class: " + c);
		}

		return (T) nativeObj.getObject();
	}

	public static IRString asString(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.STRING) {
			throw new RException("Can't convert to string: " + obj);
		}

		return (IRString) obj;
	}

	public static IRVar asVar(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.VAR) {
			throw new RException("Can't convert to var: " + obj);
		}

		return (IRVar) obj;
	}

	public static IRList buildList(List<String> strlist) throws RException {

		LinkedList<IRObject> objList = new LinkedList<>();
		for (String str : strlist) {
			objList.add(RulpFactory.createString(str));
		}

		return RulpFactory.createList(objList);
	}

	public static IRList computeAtomList(IRInterpreter intepreter, IRFrame frame, Collection<String> list)
			throws RException {

		if (list == null || list.isEmpty()) {
			return RulpFactory.createList();
		}

		LinkedList<IRObject> aList = new LinkedList<>();
		for (String element : list) {
			aList.add(intepreter.compute(frame, RulpFactory.createAtom(element)));
		}

		return RulpFactory.createList(aList);
	}

	public static boolean equal(IRObject a, IRObject b) throws RException {

		if (a == b) {
			return true;
		}

		if (a.getType() != b.getType()) {
			return false;
		}

		boolean rc = false;

		switch (a.getType()) {
		case ATOM:
		case STRING:
			rc = a.asString().equals(b.asString());
			break;

		case BOOL:
			rc = ((IRBoolean) a).asBoolean() == ((IRBoolean) b).asBoolean();
			break;

		case FLOAT:
			rc = ((IRFloat) a).asFloat() == ((IRFloat) b).asFloat();
			break;

		case INT:
			rc = ((IRInteger) a).asInteger() == ((IRInteger) b).asInteger();
			break;

		case NIL:
			return true;

		case EXPR:
		case LIST:

			IRList la = (IRList) a;
			IRList lb = (IRList) b;

			if (la.size() != lb.size()) {
				return false;
			}

			IRIterator<? extends IRObject> ia = la.iterator();
			IRIterator<? extends IRObject> ib = lb.iterator();

			while (ia.hasNext()) {
				if (!equal(ia.next(), ib.next())) {
					return false;
				}
			}

			return true;

		default:
			return false;
		}

		return rc;
	}

	public static void expectFactorParameterType(List<IRObject> args, RType type) throws RException {

		for (int i = 1; i < args.size(); ++i) {
			IRObject arg = args.get(i);
			if (arg.getType() != type) {
				throw new RException(
						String.format("the type of arg %d is not %s: %s", 1, type.toString(), args.get(1)));
			}
		}
	}

	public static IRClass findClass(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {

		case ATOM:
			IRFrameEntry entry = frame.getEntry(RulpUtility.asAtom(obj).getName());
			if (entry != null && entry.getObject() != obj) {
				return findClass(entry.getObject(), frame);
			}

			return null;

		case INSTANCE:
			return findClass(RulpUtility.asInstance(obj).getClassAtom(), frame);

		case CLASS:
			return (IRClass) obj;

		default:
			return null;
		}
	}

	public static IRAtom getObjectType(IRObject valObj) throws RException {

		IRAtom valAtom = RType.toObject(valObj.getType());
		if (valAtom == T_Instance) {
			valAtom = RulpUtility.asInstance(valObj).getClassAtom();
		}

		return valAtom;
	}

	public static boolean isAnonymousVar(String var) {
		return var.equals("?");
	}

	public static boolean isAtom(IRObject obj) {
		return obj.getType() == RType.ATOM;
	}

	public static boolean isAtom(IRObject obj, String name) {
		return obj.getType() == RType.ATOM && ((IRAtom) obj).getName().equals(name);
	}

	public static boolean isExpression(IRObject obj) {
		return obj.getType() == RType.EXPR;
	}

	public static boolean isList(IRObject obj) {
		return obj.getType() == RType.LIST;
	}

	public static boolean isPureAtomList(IRObject obj) throws RException {

		RType type = obj.getType();
		if (type != RType.LIST && type != RType.EXPR) {
			return false;
		}

		IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
		while (iter.hasNext()) {
			if (iter.next().getType() != RType.ATOM) {
				return false;
			}
		}

		return true;
	}

	public static boolean isPureAtomPairList(IRObject obj) throws RException {

		RType type = obj.getType();
		if (type != RType.LIST && type != RType.EXPR) {
			return false;
		}

		IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
		while (iter.hasNext()) {

			IRObject element = iter.next();

			if (element.getType() == RType.ATOM) {
				continue;
			} else {

			}

			if (!isPureAtomList(element) || ((IRList) element).size() != 2) {
				return false;
			}
		}

		return true;
	}

	public static boolean isQuote(IRObject obj) {

		if (obj.getType() == RType.ATOM) {
			String name = ((IRAtom) obj).getName();
			return name.equals("'") || name.equalsIgnoreCase("quote");
		} else {
			return false;
		}
	}

	public static boolean isValidRulpStmt(String line) {

		try {

			List<IRObject> rt = RulpFactory.createParser().parse(line);
			if (rt.isEmpty()) {
				return false;
			}

			for (IRObject obj : rt) {
				if (obj.getType() != RType.EXPR) {
					return false;
				}
			}

			return true;

		} catch (RException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isValueAtom(IRObject obj) {
		return obj.getType() == RType.ATOM && !isVarName(((IRAtom) obj).getName());
	}

	public static boolean isVarAtom(IRObject obj) {
		return obj.getType() == RType.ATOM && isVarName(((IRAtom) obj).getName());
	}

	public static boolean isVarName(String var) {
		return var.length() > 1 && var.charAt(0) == '?';
	}

	public static List<IRObject> load(IRInterpreter interpreter, String path, String charset) throws RException {
		try {
			return interpreter.compute(StringUtil.toOneLine(FileUtil.openTxtFile(path, charset)));
		} catch (IOException e) {
			throw new RException(e.toString());
		}
	}

	public static void loadFromJar(IRInterpreter interpreter, IRFrame sysFrame, String jarPath, String charset)
			throws RException, IOException {

		String line = null;

		synchronized (loadLineMap) {

			line = loadLineMap.get(jarPath);
			if (line == null) {
				line = StringUtil.toOneLine(FileUtil.openTxtFileFromJar(jarPath, charset));
				loadLineMap.put(jarPath, line);
			}
		}

		List<IRObject> rsts = new LinkedList<>();
		for (IRObject obj : interpreter.getParser().parse(line)) {
			rsts.add(interpreter.compute(sysFrame, obj));
		}
	}

	public static boolean matchType(IRAtom typeAtom, IRObject valObj) throws RException {

		// Match any object
		if (typeAtom == O_Nil) {
			return true;
		}

		IRAtom valAtom = RType.toObject(valObj.getType());
		if (valAtom == T_Instance) {
			valAtom = RulpUtility.asInstance(valObj).getClassAtom();
		}

		return typeAtom == valAtom;
	}

	public static ArrayList<IRObject> toArray(IRList list) throws RException {

		ArrayList<IRObject> arr = new ArrayList<>();
		IRIterator<? extends IRObject> iter = list.iterator();
		while (iter.hasNext()) {
			arr.add(iter.next());
		}
		return arr;
	}

	public static boolean toBoolean(IRObject a) throws RException {

		switch (a.getType()) {
		case NIL:
			return false;
		case BOOL:
			return ((IRBoolean) a).asBoolean();

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static <T> List<T> toList(IRIterator<T> iter) throws RException {

		List<T> list = new ArrayList<>();
		while (iter.hasNext()) {
			list.add(iter.next());
		}

		return list;
	}

	public static String toString(IRObject obj) throws RException {

		StringBuffer sb = new StringBuffer();
		_toString(sb, obj, objFormater);

		return sb.toString();
	}

	public static String toString(IRObject obj, int maxLength) throws RException {

		StringBuffer sb = new StringBuffer();
		XShortFormater formater = new XShortFormater(maxLength);
		_toString(sb, obj, formater);
		if (formater.bShort) {
			sb.append("...");
		}

		return sb.toString();
	}

	public static String toString(List<? extends IRObject> list) throws RException {

		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (IRObject e : list) {
			if (i++ != 0) {
				sb.append(' ');
			}
			_toString(sb, e, objFormater);
		}

		return sb.toString();
	}

	public static List<String> toStringList(IRObject obj) throws RException {

		LinkedList<String> list = new LinkedList<>();
		_toStringList(obj, list);

		return list;

	}

	public static List<String> toStringList(List<? extends IRObject> objList) throws RException {

		LinkedList<String> list = new LinkedList<>();
		for (IRObject obj : objList) {
			_toStringList(obj, list);
		}
		return list;
	}

	public static String toStringPrint(IRObject obj) throws RException {
		StringBuffer sb = new StringBuffer();
		_toString(sb, obj, printFormater);
		return sb.toString();
	}
}
