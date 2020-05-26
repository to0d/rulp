/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import static alpha.rulp.lang.Constant.C_ERROR;

public class XRError implements IRError {

	protected IRAtom id;

	public XRError(IRAtom id, IRObject value) {
		super();
		this.id = id;
		this.value = value;
	}

	protected IRObject value;

	@Override
	public String getInstanceName() {
		return id.getName();
	}

	@Override
	public String asString() {
		return C_ERROR + "#" + id.getName() + ":" + value;
	}

	@Override
	public RType getType() {
		return RType.INSTANCE;
	}

	@Override
	public IRAtom getId() {
		return id;
	}

	@Override
	public IRObject getValue() {
		return value;
	}

	@Override
	public String toString() {
		return asString();
	}

	@Override
	public IRAtom getClassAtom() {
		return C_ERROR;
	}
}
