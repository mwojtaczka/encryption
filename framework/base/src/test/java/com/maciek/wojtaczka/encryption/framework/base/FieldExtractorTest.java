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
	void shouldReturnOneFieldToBeEncryptedAndItsValueEqualToSensitive() {
		//given
		Entity entity = Entity.builder()
							  .nonSensitive("non sensitive")
							  .sensitive("sensitive") //one
							  .build();

		//when
		FieldExtractor.FieldsContainer<String> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		//then
		assertThat(toBeEncrypted.getFields()).hasSize(1);
		assertThat(toBeEncrypted.getIterableFields()).hasSize(0);
		Iterator<FieldWithContext<String>> iterator = toBeEncrypted.getFields()
																   .iterator();
		String sensitive = iterator.next()
								   .getValue();
		assertThat(sensitive).isEqualTo("sensitive");
	}

	@Test
	void shouldReturnNoFieldsToBeEncrypted_whenAnnotatedFieldIsNull() {
		//given
		Entity entity = Entity.builder()
							  .nonSensitive("non sensitive")
							  .build();

		//when
		FieldExtractor.FieldsContainer<String> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		//then
		assertThat(toBeEncrypted.getFields()).hasSize(0);
		assertThat(toBeEncrypted.getIterableFields()).hasSize(0);
	}

	@Test
	void shouldReturnOneIterableField() {
		//given
		Entity entity = Entity.builder()
							  .nonSensitive("non sensitive")
							  .sensitives(List.of("sensitive1", "sensitive2")) //one iterable
							  .build();

		//then
		FieldExtractor.FieldsContainer<String> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		//when
		assertThat(toBeEncrypted.getFields()).hasSize(0);
		assertThat(toBeEncrypted.getIterableFields()).hasSize(1);
		Iterator<FieldWithContext<Iterable<String>>> iterator = toBeEncrypted.getIterableFields()
																			 .iterator();
		List<String> value = (List<String>) iterator.next()
													.getValue();
		assertThat(value.get(0)).isEqualTo("sensitive1");
		assertThat(value.get(1)).isEqualTo("sensitive2");
	}

	@Test
	void shouldReturnOneStringFieldFromEmbeddedEntity() {
		//given
		EmbeddedEntity embeddedEntity = EmbeddedEntity.builder()
													  .nonSensitive("embedded non sensitive")
													  .sensitive("embedded sensitive") //one
													  .build();
		Entity entity = Entity.builder()
							  .nonSensitive("non sensitive")
							  .embeddedEntity(embeddedEntity)
							  .build();

		//when
		FieldExtractor.FieldsContainer<String> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		//then
		assertThat(toBeEncrypted.getFields()).hasSize(1);
		assertThat(toBeEncrypted.getIterableFields()).hasSize(0);
		Iterator<FieldWithContext<String>> iterator = toBeEncrypted.getFields()
																   .iterator();
		String sensitive = iterator.next()
								   .getValue();
		assertThat(sensitive).isEqualTo("embedded sensitive");
	}

	@Test
	void shouldReturnOneIterableFieldFromEmbeddedEntity() {
		//given
		EmbeddedEntity embeddedEntity = EmbeddedEntity.builder()
													  .nonSensitive("embedded non sensitive")
													  .sensitives(List.of("embedded sensitive in list")) //one iterable
													  .build();
		Entity entity = Entity.builder()
							  .nonSensitive("non sensitive")
							  .embeddedEntity(embeddedEntity)
							  .build();

		//when
		FieldExtractor.FieldsContainer<String> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);


		//then
		assertThat(toBeEncrypted.getFields()).hasSize(0);
		assertThat(toBeEncrypted.getIterableFields()).hasSize(1);
		Iterator<FieldWithContext<Iterable<String>>> iterator = toBeEncrypted.getIterableFields()
																			 .iterator();
		List<String> value = (List<String>) iterator.next()
									.getValue();
		assertThat(value.get(0)).isEqualTo("embedded sensitive in list");
	}

	@Test
	void shouldReturnTwoFieldsFromListOfEmbeddedEntities() {
		EmbeddedEntity embeddedEntity1 = EmbeddedEntity.builder()
													   .nonSensitive("embedded non sensitive")
													   .sensitive("embedded sensitive") //one
													   .build();
		EmbeddedEntity embeddedEntity2 = EmbeddedEntity.builder()
													   .nonSensitive("embedded non sensitive")
													   .sensitive("embedded sensitive") //two
													   .build();

		Entity entity = Entity.builder()
							  .nonSensitive("non sensitive")
							  .embeddedEntities(List.of(embeddedEntity1, embeddedEntity2))
							  .build();

		FieldExtractor.FieldsContainer<String> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);

		assertThat(toBeEncrypted.getFields()).hasSize(2);
		assertThat(toBeEncrypted.getIterableFields()).hasSize(0);
	}

	@Test
	void shouldReturnAllFields() {
		EmbeddedEntity embeddedEntity1 = EmbeddedEntity.builder()
													   .nonSensitive("embedded non sensitive")
													   .sensitive("sensitive") //one
													   .sensitives(List.of("sensitive")) //one iterable
													   .build();
		EmbeddedEntity embeddedEntity2 = EmbeddedEntity.builder()
													   .nonSensitive("embedded non sensitive")
													   .sensitive("sensitive") //two
													   .sensitives(List.of("sensitive")) //two iterable
													   .build();

		Entity entity = Entity.builder()
							  .nonSensitive("non sensitive")
							  .sensitive("sensitive") //three
							  .sensitives(List.of("sensitive")) //three iterable
							  .embeddedEntities(List.of(embeddedEntity1, embeddedEntity2))
							  .build();

		final Set<FieldWithContext<String>> toBeEncrypted = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class)
																		  .getFields();
		final Set<FieldWithContext<Iterable<String>>> toBeEncryptedIterable = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class)
																							.getIterableFields();

		assertThat(toBeEncrypted).hasSize(3);
		assertThat(toBeEncryptedIterable).hasSize(3);
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
