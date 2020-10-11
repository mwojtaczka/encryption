package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;
import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.stream;

class FieldExtractor {

	private static final Class<Encrypt> TO_ENCRYPT = Encrypt.class;

	public <F> FieldsContainer<F> getAllFieldsToBeEncrypted(Object entity, Class<F> clazzOfTheField) {
		FieldsContainer<F> fieldsContainer = new FieldsContainer<>();

		putAnnotatedFieldsIntoContainer(entity, clazzOfTheField, fieldsContainer);

		return fieldsContainer;
	}

	private <F> void putAnnotatedFieldsIntoContainer(Object entity, Class<F> clazzOfTheField, FieldsContainer<F> fieldsContainer) {

		Class<?> entityClass = entity.getClass();
		stream(entityClass.getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(TO_ENCRYPT))
				.forEach(field -> fieldsContainer.put(field, entity, clazzOfTheField));

		fieldsContainer.getStreamOfEmbedded()
					   .forEach(embeddedEntity -> putAnnotatedFieldsIntoContainer(embeddedEntity, clazzOfTheField, fieldsContainer));
	}

	public <F> FieldWithContext<F> getFieldByName(Object entity, String fieldName, Class<F> fieldType) throws NoSuchFieldException {
		Field declaredField = entity.getClass()
									.getDeclaredField(fieldName);
		return new FieldWithContext<>(declaredField, entity, fieldType);
	}

	public <F> FieldWithContext<Iterable<F>> getIterableFieldByName(Object entity, String fieldName, Class<F> fieldType) throws NoSuchFieldException {
		Field declaredField = entity.getClass()
									.getDeclaredField(fieldName);
		Class<Iterable<F>> iterableClass = (Class<Iterable<F>>) declaredField.getType();
		return new FieldWithContext<>(declaredField, entity, iterableClass);
	}

	static class FieldsContainer<F> {
		private final Set<FieldWithContext<F>> fields = new HashSet<>();
		private final Set<FieldWithContext<Iterable<F>>> iterableFields = new HashSet<>();
		private final Set<Object> embeddedFields = new HashSet<>();
		private final Set<Iterable<?>> iterableEmbeddedFields = new HashSet<>();

		private void put(Field field, Object context, Class<F> encryptionFieldType) {
			field.setAccessible(true);
			Object fieldValue = getFieldValue(field, context);
			Class<?> fieldType = field.getType();
			if (fieldValue == null) {
				//noinspection UnnecessaryReturnStatement
				return;
			} else if (fieldType.equals(encryptionFieldType)) {
				fields.add(new FieldWithContext<>(field, context, encryptionFieldType));
			} else if (Iterable.class.isAssignableFrom(fieldType) && areElementsOfClassOrSubclass((Iterable<?>) fieldValue, encryptionFieldType)) {
				Iterable<F> iterable = (Iterable<F>) fieldValue;
				Class<Iterable<F>> iterableClass = (Class<Iterable<F>>) iterable.getClass();
				iterableFields.add(new FieldWithContext<>(field, context, iterableClass));
			} else if (Iterable.class.isAssignableFrom(fieldType)) {
				Iterable<?> iterable = (Iterable<?>) fieldValue;
				iterableEmbeddedFields.add(iterable);
			} else {
				embeddedFields.add(fieldValue);
			}
		}

		private Object getFieldValue(Field field, Object context) {
			try {
				return field.get(context);
			} catch (IllegalAccessException e) {
				throw new EncryptionException("Unexpected error during accessing field.", e);
			}
		}

		private boolean areElementsOfClassOrSubclass(Iterable<?> iterable, Class<?> clazz) {
			Iterator<?> iterator = iterable.iterator();
			if (iterator.hasNext()) {
				Object next = iterator.next();
				return clazz.isInstance(next);
			} else {
				return false;
			}
		}

		private Stream<Object> getStreamOfEmbedded() {
			Stream<Object> streamOfEmbedded1 = Set.copyOf(embeddedFields)
												  .stream();
			Stream<Object> streamOfEmbedded2 = Set.copyOf(iterableEmbeddedFields)
												  .stream()
												  .flatMap(iterable -> StreamSupport.stream(iterable.spliterator(), false));
			embeddedFields.clear();
			iterableEmbeddedFields.clear();

			return Stream.concat(streamOfEmbedded1, streamOfEmbedded2);
		}

		Set<FieldWithContext<F>> getFields() {
			return fields;
		}

		Set<FieldWithContext<Iterable<F>>> getIterableFields() {
			return iterableFields;
		}
	}

}
