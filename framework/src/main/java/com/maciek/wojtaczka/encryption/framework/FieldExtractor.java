package com.maciek.wojtaczka.encryption.framework;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;
import com.maciek.wojtaczka.encryption.framework.annotation.Encrypt;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.stream;

class FieldExtractor {

	private static final Class<Encrypt> TO_ENCRYPT = Encrypt.class;

	public Set<FieldWithContext> getAllFieldsToBeEncrypted(Object entity) {
		return getFieldsAnnotatedBy(entity, TO_ENCRYPT)
			.collect(Collectors.toSet());
	}

	private Stream<? extends FieldWithContext> getFieldsAnnotatedBy(Object entity, Class<? extends Annotation>... annotationType) {
		return stream(entity.getClass().getDeclaredFields())
			.filter(field -> isFieldAnnotatedBy(field, annotationType))
			.map(field -> new FieldWithContext(field, entity))
			.flatMap(fieldWithContext -> recursivelyGetAllStringFieldsAnnotatedBy(fieldWithContext, annotationType));
	}

	private Stream<? extends FieldWithContext> recursivelyGetAllStringFieldsAnnotatedBy(FieldWithContext fieldWithContext,
																						Class<? extends Annotation>... annotationType) {

		try {
			Object fieldValue = fieldWithContext.getValue();
			if (fieldValue == null) {
				return Stream.of();
			} else if (isOfClassOrSubclass(fieldValue, Iterable.class) && areElementsOfClassOrSubclass((Iterable) fieldValue, String.class)) {

				return Stream.of(fieldWithContext);

			} else if (isOfClassOrSubclass(fieldValue, Iterable.class) && !areElementsOfClassOrSubclass((Iterable) fieldValue, String.class)) {

				Iterable<?> iterable = (Iterable) fieldValue;
				return StreamSupport.stream(iterable.spliterator(), false)
					.flatMap(o -> getFieldsAnnotatedBy(o, annotationType));

			} else if (isOfClassOrSubclass(fieldValue, String.class)) {

				return Stream.of(fieldWithContext);

			} else {
				return getFieldsAnnotatedBy(fieldValue, annotationType);
			}
		} catch (IllegalAccessException e) {
			throw new EncryptionException(e.getMessage(), e);
		}
	}

	private boolean isOfClassOrSubclass(Object object, Class<?> clazz) {
		return clazz.isAssignableFrom(object.getClass());
	}

	private boolean areElementsOfClassOrSubclass(Iterable iterable, Class<?> clazz) {
		Iterator iterator = iterable.iterator();
		if (iterator.hasNext()) {
			Object next = iterator.next();
			return clazz.isAssignableFrom(next.getClass());
		} else {
			return false;
		}
	}

	private boolean isFieldAnnotatedBy(Field field, Class<? extends Annotation>... annotations) {
		List<Class<? extends Annotation>> annotationList = Arrays.asList(annotations);
		return getDeclaredAnnotationsTypes(field).containsAll(annotationList);
	}

	private Set<Class<? extends Annotation>> getDeclaredAnnotationsTypes(Field field) {
		return stream(field.getDeclaredAnnotations())
			.map(Annotation::annotationType)
			.collect(Collectors.toSet());
	}

}
