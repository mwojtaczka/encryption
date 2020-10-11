package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;
import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;

import java.lang.reflect.Field;

class FieldWithContext <F> {

	private static final Class<Encrypt> TO_ENCRYPT = Encrypt.class;
	private static final String BLIND_ID = "BlindId";


	private final Field field;
	private final Object context;
	private Metadata metadata;

	FieldWithContext(Field field, Object context, Class<F> fieldType) {
		this.field = field;
		this.context = context;
	}

	F getValue() {
		this.field.setAccessible(true);
		try {
			return (F) this.field.get(this.context);
		} catch (IllegalAccessException e) {
			throw new EncryptionException("Unexpected error during accessing field.", e);
		}
	}

	void setValue(F value) {
		this.field.setAccessible(true);
		try {
			this.field.set(this.context, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	void setBlindId(Object value) throws NoSuchFieldException {
		try {
			Field blindIdField = context.getClass()
										.getDeclaredField(field.getName() + BLIND_ID);
			blindIdField.setAccessible(true);
			blindIdField.set(this.context, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	boolean isSearchable() {
		return getMetadata().isSearchable();
	}

	Metadata getMetadata() {
		if (metadata == null) {
			Encrypt annotation = field.getDeclaredAnnotation(TO_ENCRYPT);
			this.metadata = Metadata.builder()
									.lazy(annotation.lazy())
									.algorithm(annotation.algorithm())
									.searchable(annotation.searchable())
									.blindIdFieldName(field.getName() + BLIND_ID)
									.build();
		}
		return metadata;
	}

	@Builder
	@Value
	static class Metadata {
		boolean lazy;
		String algorithm;
		boolean searchable;
		String blindIdFieldName;
	}

}
