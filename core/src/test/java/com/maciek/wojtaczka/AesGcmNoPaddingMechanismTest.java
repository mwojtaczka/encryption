package com.maciek.wojtaczka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;

class AesGcmNoPaddingMechanismTest {

	private AesGcmNoPaddingMechanism cipherMechanism;

	@BeforeEach
	void setup() {
		cipherMechanism = new AesGcmNoPaddingMechanism();
	}

	@Test
	void cipher_shouldEncryptBytes() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateSecretKey();

		CipherResult cipherResult = cipherMechanism.cipher(toBeEncrypted.getBytes(), secretKey);

		Assertions.assertAll(
			() -> assertThat(cipherResult.getCipherContent()).isNotEqualTo(toBeEncrypted.getBytes()),
			() -> assertThat(cipherResult.getCipherMechanism()).isEqualTo("AES/GCM/NoPadding")
		);
	}

	private SecretKey generateSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, "AES");
	}

}
