/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRVar;
import alpha.rulp.runtime.IRVarListener;
import alpha.rulp.utility.RulpUtility;

public class XRVar implements IRVar {

	private String _asString = null;

	private IRObject value;

	private String varName;

	public XRVar(String varName, IRObject value) {
		super();
		this.varName = varName;
		this.value = value;
	}

	@Override
	public String asString() {

		if (_asString == null) {
			_asString = varName + ":\"" + value.asString() + "\"";
		}

		return _asString;
	}

	@Override
	public String getName() {
		return varName;
	}

	@Override
	public RType getType() {
		return RType.VAR;
	}

	@Override
	public IRObject getValue() {
		return value;
	}

	@Override
	public void setValue(IRObject val) throws RException {

		IRObject oldVal = this.value;
		this.value = val;
		this._asString = null;
		this.fireValueChanged(oldVal, val);
	}

	@Override
	public String toString() {
		return asString();
	}

	private List<IRVarListener> listenerList = null;

	public void fireValueChanged(IRObject oldVal, IRObject newVal) throws RException {

		if (listenerList == null) {
			return;
		}

		if (RulpUtility.equal(oldVal, newVal)) {
			return;
		}

		for (IRVarListener listener : listenerList) {
			listener.valueChanged(this, oldVal, newVal);
		}
	}

	@Override
	public void addListener(IRVarListener listener) {

		if (listenerList == null) {
			listenerList = new LinkedList<>();
		}

		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

}
