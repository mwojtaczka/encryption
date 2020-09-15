package com.maciek.wojtaczka.encryption.core.exception;

public class EncryptionException extends RuntimeException {

	public EncryptionException(String message, Exception e) {
		super(message, e);
	}


	public EncryptionException(String message) {
		super(message);
	}
}
