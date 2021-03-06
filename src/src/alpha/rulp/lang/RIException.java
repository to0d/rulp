/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import alpha.rulp.runtime.IRFrame;

public class RIException extends RException {

	private static final long serialVersionUID = -426756038895792036L;

	protected IRFrame fromFrame;

	public RIException(IRObject fromObject, IRFrame fromFrame) {
		super(fromObject);
		this.fromFrame = fromFrame;
	}
}
