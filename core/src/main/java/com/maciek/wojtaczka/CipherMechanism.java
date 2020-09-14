package com.maciek.wojtaczka;

import javax.crypto.SecretKey;

@FunctionalInterface
public interface CipherMechanism {

	CipherResult cipher(byte[] content, SecretKey secretKey);

}
