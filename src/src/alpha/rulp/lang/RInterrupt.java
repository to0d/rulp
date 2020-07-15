package alpha.rulp.lang;

import alpha.rulp.runtime.IRFrame;

public class RInterrupt extends RIException{
	
	public RInterrupt(IRObject fromObject, IRFrame fromFrame) {
		super(fromObject, fromFrame);
	}

	private static final long serialVersionUID = 4748654476903459636L;

}
