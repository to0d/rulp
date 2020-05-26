/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utility.RulpUtility;

public abstract class AbsRList implements IRList {

	protected String _asString = null;

	protected String _toString = null;

	protected RType type;

	public AbsRList(RType type) {
		super();
		this.type = type;
	}

	@Override
	public String asString() {

		if (_asString == null) {

			try {
				_asString = RulpUtility.toString(this);
			} catch (RException e) {
				e.printStackTrace();
				_asString = e.toString();
			}
		}

		return _asString;
	}

	@Override
	public RType getType() {
		return type;
	}

	@Override
	public IRIterator<? extends IRObject> iterator() {
		return listIterator(0);
	}

	@Override
	public String toString() {

		if (_toString == null) {

			try {
				_toString = RulpUtility.toString(this, MAX_TOSTRING_LEN);
			} catch (RException e) {
				e.printStackTrace();
				_toString = e.toString();
			}
		}

		return _toString;
	}

}
