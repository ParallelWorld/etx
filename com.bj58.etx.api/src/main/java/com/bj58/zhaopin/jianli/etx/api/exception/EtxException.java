package com.bj58.zhaopin.jianli.etx.api.exception;

public class EtxException extends RuntimeException {

	private static final long serialVersionUID = -2749675758651784005L;

	public EtxException() {
		super();
	}

	public EtxException(String message, Throwable cause) {
		super(message, cause);
	}

	public EtxException(String message) {
		super(message);
	}

	public EtxException(Throwable cause) {
		super(cause);
	}
}
