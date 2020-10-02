package com.maciek.wojtaczka.encryption.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;

import static java.nio.charset.StandardCharsets.UTF_16;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HmacSha256MechanismTest {

	private static final Charset CHARSET = UTF_16;

	private HmacSha256Mechanism cipherMechanism;

	@BeforeEach
	void setup() {
		cipherMechanism = new HmacSha256Mechanism();
	}

	@Test
	void shouldHashInput() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateAesSecretKey();

		CipherResult cipherResult = cipherMechanism.encrypt(toBeEncrypted.getBytes(CHARSET), secretKey);

		assertAll(
				() -> assertThat(cipherResult.getCipherContent()).isNotEqualTo(toBeEncrypted.getBytes(CHARSET)),
				() -> assertThat(cipherResult.getCipherMechanism()).isEqualTo("HmacSHA256")
		);
	}

	@Test
	void shouldHashInDeterministicManner() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateAesSecretKey();

		CipherResult cipherResult1 = cipherMechanism.encrypt(toBeEncrypted.getBytes(CHARSET), secretKey);
		CipherResult cipherResult2 = cipherMechanism.encrypt(toBeEncrypted.getBytes(CHARSET), secretKey);

		assertThat(cipherResult1.getCipherContent()).isEqualTo(cipherResult2.getCipherContent());
	}

	private SecretKey generateAesSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, "AES");
	}
}
