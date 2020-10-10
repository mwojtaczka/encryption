package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FieldExtractorTest {

	private FieldExtractor fieldExtractor;

	@BeforeEach
	void setup() {
		fieldExtractor = new FieldExtractor();
	}

	@Test
	void shouldReturnOneFieldToBeEncryptedAndItsValueEqualToSensitive() throws IllegalAccessException {
		Entity entity = Entity.builder()
			.nonSensitive("non sensitive")
			.sensitive("sensitive")
			.build();

		final Set<FieldWithContext> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		assertThat(toBeEncrypted).hasSize(1);
		Iterator<FieldWithContext> iterator = toBeEncrypted.iterator();
		String sensitive = (String) iterator.next().getValue();
		assertThat(sensitive).isEqualTo("sensitive");
	}

	@Test
	void shouldReturnNoFieldsToBeEncrypted_whenAnnotatedFieldIsNull() {
		Entity entity = Entity.builder()
			.nonSensitive("non sensitive")
			.build();

		final Set<FieldWithContext> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		assertThat(toBeEncrypted).hasSize(0);
	}

	@Test
	void shouldReturnOneIterableField() {
		Entity entity = Entity.builder()
			.nonSensitive("non sensitive")
			.sensitives(List.of("sensitive1", "sensitive2"))
			.build();

		final Set<FieldWithContext> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		assertThat(toBeEncrypted).hasSize(1);
		Iterator<FieldWithContext> iterator = toBeEncrypted.iterator();
		List value = (List) iterator.next().getValue();
		assertThat(value.get(0)).isEqualTo("sensitive1");
		assertThat(value.get(1)).isEqualTo("sensitive2");
	}

	@Test
	void shouldReturnOneStringFieldFromEmbeddedEntity() {
		EmbeddedEntity embeddedEntity = EmbeddedEntity.builder()
			.nonSensitive("embedded non sensitive")
			.sensitive("embedded sensitive")
			.build();
		Entity entity = Entity.builder()
			.nonSensitive("non sensitive")
			.embeddedEntity(embeddedEntity)
			.build();

		final Set<FieldWithContext> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		assertThat(toBeEncrypted).hasSize(1);
		Iterator<FieldWithContext> iterator = toBeEncrypted.iterator();
		String sensitive = (String) iterator.next().getValue();
		assertThat(sensitive).isEqualTo("embedded sensitive");
	}

	@Test
	void shouldReturnOneIterableFieldFromEmbeddedEntity() {
		EmbeddedEntity embeddedEntity = EmbeddedEntity.builder()
			.nonSensitive("embedded non sensitive")
			.sensitives(List.of("embedded sensitive in list"))
			.build();
		Entity entity = Entity.builder()
			.nonSensitive("non sensitive")
			.embeddedEntity(embeddedEntity)
			.build();

		final Set<FieldWithContext> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		assertThat(toBeEncrypted).hasSize(1);
		Iterator<FieldWithContext> iterator = toBeEncrypted.iterator();
		List value = (List) iterator.next().getValue();
		assertThat(value.get(0)).isEqualTo("embedded sensitive in list");
	}

	@Test
	void shouldReturnTwoFieldsFromListOfEmbeddedEntities() {
		EmbeddedEntity embeddedEntity1 = EmbeddedEntity.builder()
			.nonSensitive("embedded non sensitive")
			.sensitive("embedded sensitive")
			.build();
		EmbeddedEntity embeddedEntity2 = EmbeddedEntity.builder()
			.nonSensitive("embedded non sensitive")
			.sensitive("embedded sensitive")
			.build();

		Entity entity = Entity.builder()
			.nonSensitive("non sensitive")
			.embeddedEntities(List.of(embeddedEntity1, embeddedEntity2))
			.build();

		final Set<FieldWithContext> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		assertThat(toBeEncrypted).hasSize(2);
	}

	@Test
	void shouldReturnAllFields() {
		EmbeddedEntity embeddedEntity1 = EmbeddedEntity.builder()
			.nonSensitive("embedded non sensitive")
			.sensitive("sensitive")
			.sensitives(List.of("sensitive"))
			.build();
		EmbeddedEntity embeddedEntity2 = EmbeddedEntity.builder()
			.nonSensitive("embedded non sensitive")
			.sensitive("sensitive")
			.sensitives(List.of("sensitive"))
			.build();

		Entity entity = Entity.builder()
			.nonSensitive("non sensitive")
			.sensitive("sensitive")
			.sensitives(List.of("sensitive"))
			.embeddedEntities(List.of(embeddedEntity1, embeddedEntity2))
			.build();

		final Set<FieldWithContext> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		assertThat(toBeEncrypted).hasSize(6);
	}

	@Data
	@Builder
	private static class Entity {
		String nonSensitive;
		@Encrypt
		String sensitive;
		@Encrypt
		List<String> sensitives;
		@Encrypt
		EmbeddedEntity embeddedEntity;
		@Encrypt
		List<EmbeddedEntity> embeddedEntities;
	}

	@Data
	@Builder
	private static class EmbeddedEntity {
		String nonSensitive;
		@Encrypt
		String sensitive;
		@Encrypt
		List<String> sensitives;
	}

}
