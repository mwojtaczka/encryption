package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;

import java.lang.reflect.Field;

class FieldWithContext {

	private static final Class<Encrypt> TO_ENCRYPT = Encrypt.class;
	private static final String BLIND_ID = "BlindId";


	private final Field field;
	private final Object context;
	private Metadata metadata;

	FieldWithContext(Field field, Object context) {
		this.field = field;
		this.context = context;
	}

	Object getValue() throws IllegalAccessException {
		this.field.setAccessible(true);
		return this.field.get(this.context);
	}

	void setValue(Object value) throws IllegalAccessException {
		this.field.setAccessible(true);
		this.field.set(this.context, value);
	}

	void setBlindId(Object value) throws IllegalAccessException, NoSuchFieldException {
		Field blindIdField = context.getClass()
									 .getDeclaredField(field.getName() + BLIND_ID);
		blindIdField.setAccessible(true);
		blindIdField.set(this.context, value);
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
		String blindIdAlgorithm;
	}

}
