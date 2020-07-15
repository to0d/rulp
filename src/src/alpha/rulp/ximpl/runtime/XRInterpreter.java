/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RIException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFrame;
import alpha.rulp.runtime.IRFrameEntry;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IROut;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utility.RulpFactory;

public class XRInterpreter implements IRInterpreter {

	public static boolean TRACE = false;

	private IROut out;

	private IRParser parser;

	private IRFrame runtimeFrame;

	private IRFrame sysFrame;

	public XRInterpreter() throws RException {
		super();
	}

	@Override
	public void addObject(IRObject obj) throws RException {

		switch (obj.getType()) {
		case NIL:
		case ATOM:
			IRAtom atom = (IRAtom) obj;
			runtimeFrame.setEntry(atom.getName(), atom);
			break;

		case BOOL:
			IRBoolean bv = (IRBoolean) obj;
			runtimeFrame.setEntry(bv.asString(), bv);
			break;

		case FACTOR:
			IRFactor factor = (IRFactor) obj;
			runtimeFrame.setEntry(factor.getName(), factor);
			break;

		case INSTANCE:
			IRInstance cv = (IRInstance) obj;
			runtimeFrame.setEntry(cv.getInstanceName(), cv);
			break;

		default:
			throw new RException("Invalid object: " + obj);
		}

	}

	@Override
	public IRObject compute(IRFrame curFrame, IRObject obj) throws RException {
		return RuntimeUtils.compute(obj, this, curFrame);
	}

	@Override
	public List<IRObject> compute(String input) throws RException {

		try {

			List<IRObject> rsts = new LinkedList<>();
			IRParser _parser = this.getParser();
			for (IRObject obj : _parser.parse(input)) {
				rsts.add(compute(runtimeFrame, obj));
			}
			return rsts;

		} catch (RIException e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException("Unhandled internal exception: " + e.toString());

		} catch (RError e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException("Unhandled error: id=\"" + e.getError() + "\", from=\"" + e.getFromObject() + "\"");
		}
	}

	@Override
	public IRObject getObject(String name) throws RException {
		IRFrameEntry entry = runtimeFrame.getEntry(name);
		return entry == null ? null : entry.getObject();
	}

	@Override
	public IROut getOut() {
		return out;
	}

	@Override
	public IRParser getParser() {
		if (parser == null) {
			parser = RulpFactory.createParser();
		}
		return parser;
	}

	@Override
	public IRFrame getRuntimeFrame() {
		return runtimeFrame;
	}

	public IRFrame getSystemFrame() {
		return sysFrame;
	}

	@Override
	public void out(String line) {

		if (out != null) {
			out.out(line);
		} else {
			System.out.print(line);
		}
	}

	@Override
	public void setOutput(IROut out) {
		this.out = out;
	}

	public void setSystemFrame(IRFrame sysFrame) {
		this.sysFrame = sysFrame;
		this.runtimeFrame = RulpFactory.createdChildFrame(sysFrame, "RUN");
	}
}
