package com.maciek.wojtaczka;

import com.maciek.wojtaczka.exception.EncryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

class AesGcmNoPaddingMechanismTest {

	private AesGcmNoPaddingMechanism cipherMechanism;

	@BeforeEach
	void setup() {
		cipherMechanism = new AesGcmNoPaddingMechanism();
	}

	@Test
	void shouldEncryptInput() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateAesSecretKey();

		CipherResult cipherResult = cipherMechanism.encrypt(toBeEncrypted.getBytes(), secretKey);

		assertAll(
			() -> assertThat(cipherResult.getCipherContent()).isNotEqualTo(toBeEncrypted.getBytes()),
			() -> assertThat(cipherResult.getCipherMechanism()).isEqualTo("AES/GCM/NoPadding")
		);
	}

	@Test
	void shouldEncryptAndDecryptInput() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateAesSecretKey();

		CipherResult cipherResult = cipherMechanism.encrypt(toBeEncrypted.getBytes(), secretKey);
		byte[] cipherContent = cipherResult.getCipherContent();
		EncryptionMaterials encryptionMaterials = EncryptionMaterials.of(secretKey, cipherResult.getIv());

		byte[] decryptedContent = cipherMechanism.decrypt(cipherContent, encryptionMaterials);

		assertThat(decryptedContent).isEqualTo(toBeEncrypted.getBytes());
	}

	@Test
	void shouldEncryptInNondeterministicManner() {
		String toBeEncrypted = "foo_boo";
		SecretKey secretKey = generateAesSecretKey();

		CipherResult cipherResult1 = cipherMechanism.encrypt(toBeEncrypted.getBytes(), secretKey);
		CipherResult cipherResult2 = cipherMechanism.encrypt(toBeEncrypted.getBytes(), secretKey);

		assertThat(cipherResult1.getCipherContent()).isNotEqualTo(cipherResult2.getCipherContent());
	}

	@Test
	void shouldThrowEncryptionException_whenIvSizeIsNot16() {
		byte[] incorrectIv = new byte[15];
		EncryptionMaterials encryptionMaterials = EncryptionMaterials.of(generateAesSecretKey(), incorrectIv);
		byte[] cipherContent = new byte[1];

		Throwable throwable = catchThrowable(() -> cipherMechanism.decrypt(cipherContent, encryptionMaterials));

		assertAll(
			() -> assertThat(throwable).isInstanceOf(EncryptionException.class),
			() -> assertThat(throwable.getMessage()).isEqualTo("Invalid IV length for AES/GCM/NoPadding mechanism. Should be: 12 but is: 15")
		);
	}

	private SecretKey generateAesSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, "AES");
	}

}
