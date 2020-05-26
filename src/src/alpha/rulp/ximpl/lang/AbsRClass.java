/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.RType;

public abstract class AbsRClass implements IRClass {

	private IRAtom classAtom;

	public AbsRClass(IRAtom classAtom) {
		super();
		this.classAtom = classAtom;
	}

	@Override
	public String asString() {
		return classAtom.getName();
	}

	@Override
	public RType getType() {
		return RType.CLASS;
	}

	@Override
	public String toString() {
		return asString();
	}

	@Override
	public IRAtom getClassAtom() {
		return classAtom;
	}
}
