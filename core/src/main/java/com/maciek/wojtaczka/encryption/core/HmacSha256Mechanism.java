package com.maciek.wojtaczka.encryption.core;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacSha256Mechanism implements CipherMechanism{

	public static final String HMAC_SHA_256 = "HmacSHA256";

	@Override
	public String getType() {
		return HMAC_SHA_256;
	}

	@Override
	public CipherResult encrypt(byte[] content, SecretKey secretKey) {
		try {
			Mac sha256Hmac = Mac.getInstance(HMAC_SHA_256);
			sha256Hmac.init(secretKey);
			byte[] hashed = sha256Hmac.doFinal(content);

			return CipherResult.of(hashed, HMAC_SHA_256);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new EncryptionException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] decrypt(byte[] cipherContent, SecretKey secretKey) {
		throw new UnsupportedOperationException("Decrypting not available for " + HMAC_SHA_256);
	}
}
