package com.maciek.wojtaczka.encryption.core;

import javax.crypto.SecretKey;

public interface CipherMechanism {

	String getType();

	CipherResult encrypt(byte[] content, SecretKey secretKey);

	byte[] decrypt(byte[] cipherContent, SecretKey secretKey);

}
