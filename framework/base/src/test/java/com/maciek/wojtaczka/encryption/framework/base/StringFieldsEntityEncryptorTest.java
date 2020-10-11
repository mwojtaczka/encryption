package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.AesGcmNoPaddingMechanism;
import com.maciek.wojtaczka.encryption.core.CipherMechanism;
import com.maciek.wojtaczka.encryption.core.EncryptionFacade;
import com.maciek.wojtaczka.encryption.core.EncryptionKey;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;
import com.maciek.wojtaczka.encryption.core.HmacSha256Mechanism;
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
class StringFieldsEntityEncryptorTest {

	@Mock
	private EncryptionKeyProvider keyProvider;

	@Mock
	KeyNameResolver keyNameResolver;

	private EntityEncryptor<String> entityEncryptor;

	@BeforeEach
	void setup() {
		CipherMechanism aesGcmNoPaddingMechanism = new AesGcmNoPaddingMechanism();
		CipherMechanism hmacShaMechanism = new HmacSha256Mechanism();
		EncryptionFacade encryptionFacade = new EncryptionFacade(Set.of(aesGcmNoPaddingMechanism, hmacShaMechanism), keyProvider);
		FieldEncryptor<String> stringEncryptor = new StringEncryptor(encryptionFacade);
		entityEncryptor = new GenericEntityEncryptor<>(stringEncryptor, keyNameResolver, "HmacSHA256", String.class);
	}

	@Test
	void shouldNotContainOriginalValue() {
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getKey("test_key", 1, "AES/GCM/NoPadding")).thenReturn(testKey);
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
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		Entity entity = Entity.builder()
			.lazySensitive("sensitive")
			.build();

		entityEncryptor.encryptObject(entity, "test_key");
		EncryptionException thrown = catchThrowableOfType(entity::getDecryptedLazySensitiveWrongFieldName, EncryptionException.class);

		assertThat(thrown).hasMessage("No such field found");
	}

	@Test
	void shouldFillBlindIdField() {
		EncryptionKey testKey = EncryptionKey.of("test_key", generateAesSecretKey(), 1);
		when(keyNameResolver.resolveBlindIdKeyName()).thenReturn("test_key");
		when(keyProvider.getLatestKey("test_key", "AES/GCM/NoPadding")).thenReturn(testKey);
		when(keyProvider.getLatestKey("test_key", "HmacSHA256")).thenReturn(testKey);
		Entity entity = Entity.builder()
							  .searchableSensitive("sensitive")
							  .build();

		entityEncryptor.encryptObject(entity, "test_key");
		assertThat(entity.getSearchableSensitiveBlindId()).isNotBlank();
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
		@Encrypt(searchable = true)
		String searchableSensitive;
		String searchableSensitiveBlindId;

		String getDecryptedLazySensitive() {
			StaticDecryptor.decryptField(this, "lazySensitive", "test_key");
			return lazySensitive;
		}

		String getDecryptedLazySensitiveWrongFieldName() {
			StaticDecryptor.decryptField(this, "wrongName", "test_key");
			return lazySensitive;
		}

		List<String> getDecryptedLazySensitiveList() {
			StaticDecryptor.decryptIterableField(this, "lazySensitiveList", "test_key");
			return lazySensitiveList;
		}

		Set<String> getDecryptedLazySensitiveSet() {
			StaticDecryptor.decryptIterableField(this, "lazySensitiveSet", "test_key");
			return lazySensitiveSet;
		}

	}

	@Builder
	@Value
	private static class EmbeddedEntity {

		@Encrypt
		String sensitive;
	}


}
