package com.maciek.wojtaczka.encryption.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;

import static java.nio.charset.StandardCharsets.UTF_16;
import static org.assertj.core.api.Assertions.assertThat;

class AesGcmNoPaddingMechanismTest {

	private static final Charset CHARSET = UTF_16;

	private AesGcmNoPaddingMechanism cipherMechanism;

	@BeforeEach
	void setup() {
		cipherMechanism = new AesGcmNoPaddingMechanism();
	}

	@Test
	void shouldEncryptInput() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateAesSecretKey();

		byte[] cipherResult = cipherMechanism.encrypt(toBeEncrypted.getBytes(CHARSET), secretKey);

		assertThat(cipherResult).isNotEqualTo(toBeEncrypted.getBytes(CHARSET));
	}

	@Test
	void shouldEncryptAndDecryptInput() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateAesSecretKey();

		byte[] cipherResult = cipherMechanism.encrypt(toBeEncrypted.getBytes(CHARSET), secretKey);

		byte[] decryptedContent = cipherMechanism.decrypt(cipherResult, secretKey);

		assertThat(decryptedContent).isEqualTo(toBeEncrypted.getBytes(CHARSET));
	}

	@Test
	void shouldEncryptInNondeterministicManner() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateAesSecretKey();

		byte[] cipherResult1 = cipherMechanism.encrypt(toBeEncrypted.getBytes(CHARSET), secretKey);
		byte[] cipherResult2 = cipherMechanism.encrypt(toBeEncrypted.getBytes(CHARSET), secretKey);

		assertThat(cipherResult1).isNotEqualTo(cipherResult2);
	}


	private SecretKey generateAesSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, "AES");
	}

}
