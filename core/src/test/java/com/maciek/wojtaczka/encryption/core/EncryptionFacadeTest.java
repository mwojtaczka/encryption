package com.maciek.wojtaczka.encryption.core;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_16;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptionFacadeTest {

	private static final Charset CHARSET = UTF_16;

	@Mock
	private EncryptionKeyProvider keyProvider;

	private EncryptionFacade encryptionFacade;

	@BeforeEach
	void setup() {
		CipherMechanism cipherMechanism = new AesGcmNoPaddingMechanism();
		encryptionFacade = new EncryptionFacade(Set.of(cipherMechanism), keyProvider);
	}

	@Test
	void shouldNotContainPlainValue() {
		String toBeEncrypted = "foo_boo";
		byte[] toBeEncryptedBytes = toBeEncrypted.getBytes(CHARSET);
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);

		CipherRecord cipherRecord = encryptionFacade.encryptBytes(toBeEncryptedBytes, "test_key", "AES/GCM/NoPadding");

		assertThat(cipherRecord.getCipherContent()).isNotEqualTo(toBeEncryptedBytes);
	}

	@Test
	void shouldContainEncryptedValueAndMetadata() {
		String toBeEncrypted = "foo_boo";
		byte[] toBeEncryptedBytes = toBeEncrypted.getBytes(CHARSET);
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);

		CipherRecord cipherRecord = encryptionFacade.encryptBytes(toBeEncryptedBytes, "test_key", "AES/GCM/NoPadding");

		assertAll(
			() -> assertThat(cipherRecord.getCipherContent()).isNotNull(),
			() -> assertThat(cipherRecord.getCipherMechanismType()).isEqualTo("AES/GCM/NoPadding"),
			() -> assertThat(cipherRecord.getEncryptionKeyName()).isEqualTo("test_key"),
			() -> assertThat(cipherRecord.getEncryptionKeyVersion()).isEqualTo(1)
		);
	}

	@Test
	void shouldEncryptAndDecrypt() {

		String toBeEncrypted = "foo_boo";
		byte[] toBeEncryptedBytes = toBeEncrypted.getBytes(CHARSET);
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1)).thenReturn(test_key);

		CipherRecord cipherRecord = encryptionFacade.encryptBytes(toBeEncryptedBytes, "test_key", "AES/GCM/NoPadding");
		byte[] decrypted = encryptionFacade.decryptRecord(cipherRecord, "test_key", "AES/GCM/NoPadding");

		assertThat(decrypted).isEqualTo(toBeEncryptedBytes);
	}

	@Test
	void shouldEncryptAndDecryptRandomUtf16String() {

		String toBeEncrypted = RandomStringUtils.random(50);
		byte[] toBeEncryptedBytes = toBeEncrypted.getBytes(CHARSET);
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1)).thenReturn(test_key);

		CipherRecord cipherRecord = encryptionFacade.encryptBytes(toBeEncryptedBytes, "test_key", "AES/GCM/NoPadding");
		byte[] decrypted = encryptionFacade.decryptRecord(cipherRecord, "test_key", "AES/GCM/NoPadding");

		assertThat(decrypted).isEqualTo(toBeEncryptedBytes);
	}

	private SecretKey generateAesSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, "AES");
	}


}
