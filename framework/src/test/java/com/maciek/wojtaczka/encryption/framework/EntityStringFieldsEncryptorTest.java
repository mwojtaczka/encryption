package com.maciek.wojtaczka.encryption.framework;

import com.maciek.wojtaczka.encryption.core.AesGcmNoPaddingMechanism;
import com.maciek.wojtaczka.encryption.core.CipherMechanism;
import com.maciek.wojtaczka.encryption.core.EncryptionFacade;
import com.maciek.wojtaczka.encryption.core.EncryptionKey;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;
import com.maciek.wojtaczka.encryption.framework.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;
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
class EntityStringFieldsEncryptorTest {

	@Mock
	EncryptionKeyProvider keyProvider;

	EntityStringFieldsEncryptor entityEncryptor;

	@BeforeEach
	void setup() {
		CipherMechanism aesGcmNoPaddingMechanism = new AesGcmNoPaddingMechanism();
		EncryptionFacade encryptionFacade = new EncryptionFacade(Set.of(aesGcmNoPaddingMechanism), keyProvider);
		FieldEncryptor<String> stringEncryptor = new StringEncryptor(encryptionFacade);
		FieldExtractor fieldExtractor = new FieldExtractor();
		entityEncryptor = new EntityStringFieldsEncryptor(stringEncryptor, fieldExtractor);
	}

	@Test
	void shouldNotContainOriginalValue() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);
		Entity entity = Entity.builder()
			.sensitive1("sensitive1")
			.sensitive2("sensitive2")
			.build();

		entityEncryptor.encryptObject(entity, "test_key");

		assertAll(
			() -> assertThat(entity.getSensitive1()).doesNotContain("sensitive1"),
			() -> assertThat(entity.getSensitive2()).doesNotContain("sensitive2")
		);
	}

	@Test
	void shouldEncryptAndDecryptEntityStringFields() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1)).thenReturn(test_key);
		Entity entity = Entity.builder()
			.sensitive1("sensitive1")
			.sensitive2("sensitive2")
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getSensitive1()).doesNotContain("sensitive1"),
			() -> assertThat(entity.getSensitive2()).doesNotContain("sensitive2")
		);

		entityEncryptor.decryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getSensitive1()).isEqualTo("sensitive1"),
			() -> assertThat(entity.getSensitive2()).isEqualTo("sensitive2")
		);
	}

	private SecretKey generateAesSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, "AES");
	}

	@Builder
	@Value
	private static class Entity {

		@Encrypt
		String sensitive1;
		@Encrypt
		String sensitive2;
	}


}
