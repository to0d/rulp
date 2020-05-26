/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime.factor;

import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;

public abstract class AbsRFactorAdapter implements IRFactor {

	private String factorName;

	public AbsRFactorAdapter(String factorName) {
		super();
		this.factorName = factorName;
	}

	@Override
	public String asString() {
		return factorName;
	}

	@Override
	public String getName() {
		return factorName;
	}

	@Override
	public RType getType() {
		return RType.FACTOR;
	}

	@Override
	public String toString() {
		return factorName;
	}
}
