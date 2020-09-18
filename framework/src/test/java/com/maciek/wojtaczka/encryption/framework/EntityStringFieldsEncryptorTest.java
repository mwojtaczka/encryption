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
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityStringFieldsEncryptorTest {

	@Mock
	private EncryptionKeyProvider keyProvider;

	private EntityStringFieldsEncryptor entityEncryptor;

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
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
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
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
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

	@Test
	void shouldEncryptAndDecryptEntityStringListField() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
		Entity entity = Entity.builder()
			.sensitiveList(List.of("sensitive"))
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getSensitiveList()).doesNotContain("sensitive")
		);

		entityEncryptor.decryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getSensitiveList()).containsOnly("sensitive")
		);
	}

	@Test
	void shouldEncryptAndDecryptEntityStringSetField() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
		Entity entity = Entity.builder()
			.sensitiveSet(Set.of("sensitive"))
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getSensitiveSet()).doesNotContain("sensitive")
		);

		entityEncryptor.decryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getSensitiveSet()).containsOnly("sensitive")
		);
	}

	@Test
	void shouldEncryptAndDecryptEntityEmbeddedEntityField() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
		EmbeddedEntity embeddedEntity = EmbeddedEntity.builder()
			.sensitive("sensitive")
			.build();
		Entity entity = Entity.builder()
			.embeddedEntity(embeddedEntity)
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getEmbeddedEntity().getSensitive()).doesNotContain("sensitive")
		);

		entityEncryptor.decryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getEmbeddedEntity().getSensitive()).isEqualTo("sensitive")
		);
	}

	@Test
	void shouldEncryptAndDecryptEntityEmbeddedEntityListField() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
		EmbeddedEntity embeddedEntity = EmbeddedEntity.builder()
			.sensitive("sensitive")
			.build();
		Entity entity = Entity.builder()
			.embeddedEntityList(List.of(embeddedEntity))
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getEmbeddedEntityList()).allMatch(element -> !element.getSensitive().contains("sensitive"))
		);

		entityEncryptor.decryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getEmbeddedEntityList()).allMatch(element -> element.getSensitive().equals("sensitive"))
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
		@Encrypt
		List<String> sensitiveList;
		@Encrypt
		Set<String> sensitiveSet;
		@Encrypt
		EmbeddedEntity embeddedEntity;
		@Encrypt
		List<EmbeddedEntity> embeddedEntityList;
	}

	@Builder
	@Value
	private static class EmbeddedEntity {

		@Encrypt
		String sensitive;
	}


}
