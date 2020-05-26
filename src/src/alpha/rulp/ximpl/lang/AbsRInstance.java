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
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.RType;

public class AbsRInstance implements IRInstance {

	protected IRAtom classAtom;

	protected String instanceName;

	public AbsRInstance(IRAtom classAtom, String instanceName) {
		super();
		this.classAtom = classAtom;
		this.instanceName = instanceName;
	}

	@Override
	public String asString() {
		return instanceName;
	}

	@Override
	public IRAtom getClassAtom() {
		return classAtom;
	}

	@Override
	public String getInstanceName() {
		return instanceName;
	}

	@Override
	public RType getType() {
		return RType.INSTANCE;
	}

	@Override
	public String toString() {
		return asString();
	}

}
