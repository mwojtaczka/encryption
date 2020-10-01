package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.AesGcmNoPaddingMechanism;
import com.maciek.wojtaczka.encryption.core.CipherMechanism;
import com.maciek.wojtaczka.encryption.core.EncryptionFacade;
import com.maciek.wojtaczka.encryption.core.EncryptionKey;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;
import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;
import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
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
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityStringFieldsEncryptorTest {

	@Mock
	private EncryptionKeyProvider keyProvider;

	@Mock
	KeyNameResolver keyNameResolver;

	private EntityEncryptor<String> entityEncryptor;

	@BeforeEach
	void setup() {
		CipherMechanism aesGcmNoPaddingMechanism = new AesGcmNoPaddingMechanism();
		EncryptionFacade encryptionFacade = new EncryptionFacade(Set.of(aesGcmNoPaddingMechanism), keyProvider);
		FieldEncryptor<String> stringEncryptor = new StringEncryptor(encryptionFacade);
		entityEncryptor = new EntityStringFieldsEncryptor(stringEncryptor, keyNameResolver);
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

	@Test
	void shouldEncryptAndThenDecryptOnlyEagerFields() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
		Entity entity = Entity.builder()
			.sensitive1("sensitive")
			.lazySensitive("sensitive")
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getSensitive1()).doesNotContain("sensitive"),
			() -> assertThat(entity.getLazySensitive()).doesNotContain("sensitive")
		);

		entityEncryptor.decryptObject(entity, "test_key");
		assertAll(
			() -> assertThat(entity.getSensitive1()).isEqualTo("sensitive"),
			() -> assertThat(entity.getLazySensitive()).doesNotContain("sensitive")
		);
	}

	@Test
	void shouldEncryptAndDecryptLazyField() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
		Entity entity = Entity.builder()
			.lazySensitive("sensitive")
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertThat(entity.getLazySensitive()).doesNotContain("sensitive");

		String decryptedLazySensitive = entity.getDecryptedLazySensitive();
		assertThat(decryptedLazySensitive).isEqualTo("sensitive");
	}

	@Test
	void shouldEncryptAndDecryptLazyListField() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
		Entity entity = Entity.builder()
							  .lazySensitiveList(List.of("sensitive"))
							  .build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertThat(entity.getLazySensitiveList()).doesNotContain("sensitive");

		List<String> decryptedLazySensitiveList = entity.getDecryptedLazySensitiveList();
		assertThat(decryptedLazySensitiveList).containsExactly("sensitive");
	}

	@Test
	void shouldEncryptAndDecryptLazySetField() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(test_key);
		Entity entity = Entity.builder()
							  .lazySensitiveSet(Set.of("sensitive"))
							  .build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertThat(entity.getLazySensitiveSet()).doesNotContain("sensitive");

		Set<String> decryptedLazySensitiveSet = entity.getDecryptedLazySensitiveSet();
		assertThat(decryptedLazySensitiveSet).containsExactly("sensitive");
	}

	@Test
	void shouldThrowEncryptionException_whenFieldNameDoesNotMatchToAnyFields() {
		EncryptionKey test_key = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(test_key);
		Entity entity = Entity.builder()
			.lazySensitive("sensitive")
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertThat(entity.getLazySensitive()).doesNotContain("sensitive");

		EncryptionException thrown = catchThrowableOfType(entity::getDecryptedLazySensitiveWrongFieldName, EncryptionException.class);
		assertThat(thrown).hasMessage("No such field found");
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
		@Encrypt(lazy = true)
		String lazySensitive;
		@Encrypt(lazy = true)
		List<String> lazySensitiveList;
		@Encrypt(lazy = true)
		Set<String> lazySensitiveSet;

		String getDecryptedLazySensitive() {
			return StaticDecryptor.decryptField(this, "lazySensitive", "test_key");
		}

		String getDecryptedLazySensitiveWrongFieldName() {
			return StaticDecryptor.decryptField(this, "wrongName", "test_key");
		}

		List<String> getDecryptedLazySensitiveList() {
			return StaticDecryptor.decryptListField(this, "lazySensitiveList", "test_key");
		}

		Set<String> getDecryptedLazySensitiveSet() {
			return StaticDecryptor.decryptSetField(this, "lazySensitiveSet", "test_key");
		}

	}

	@Builder
	@Value
	private static class EmbeddedEntity {

		@Encrypt
		String sensitive;
	}


}