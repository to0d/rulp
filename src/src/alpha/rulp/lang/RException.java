package alpha.rulp.lang;
/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

public class RException extends Exception {

	private static final long serialVersionUID = -5868320437069359308L;

	protected IRObject fromObject;

	public RException() {
		super();
	}

	public RException(IRObject fromObject) {
		super();
		this.fromObject = fromObject;
	}

	public RException(IRObject fromObject, String message) {
		super(message);
		this.fromObject = fromObject;
	}

	public RException(String message) {
		super(message);
	}

	public IRObject getFromObject() {
		return fromObject;
	}

}