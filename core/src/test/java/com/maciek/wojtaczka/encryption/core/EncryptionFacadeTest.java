package com.maciek.wojtaczka.encryption.core;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptionFacadeTest {

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
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);

		String encrypted = encryptionFacade.encryptString(toBeEncrypted, "test_key", "AES/GCM/NoPadding");
		CipherRecord cipherRecord = CipherRecord.of(encrypted);

		assertThat(encrypted).doesNotContain("foo_boo");
	}

	@Test
	void shouldContainEncryptedValueAndMetadata() {
		String toBeEncrypted = "foo_boo";
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);

		String encrypted = encryptionFacade.encryptString(toBeEncrypted, "test_key", "AES/GCM/NoPadding");
		CipherRecord cipherRecord = CipherRecord.of(encrypted);

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
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1)).thenReturn(test_key);

		String encrypted = encryptionFacade.encryptString(toBeEncrypted, "test_key", "AES/GCM/NoPadding");
		String decrypted = encryptionFacade.decryptString(encrypted, "test_key", "AES/GCM/NoPadding");

		assertThat(decrypted).isEqualTo(toBeEncrypted);
	}

	@Test
	void shouldEncryptAndDecryptRandomUtf16String() {

		String toBeEncrypted = RandomStringUtils.random(50);
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1)).thenReturn(test_key);

		String encrypted = encryptionFacade.encryptString(toBeEncrypted, "test_key", "AES/GCM/NoPadding");
		String decrypted = encryptionFacade.decryptString(encrypted, "test_key", "AES/GCM/NoPadding");

		assertThat(decrypted).isEqualTo(toBeEncrypted);
	}

	private SecretKey generateAesSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, "AES");
	}


}
