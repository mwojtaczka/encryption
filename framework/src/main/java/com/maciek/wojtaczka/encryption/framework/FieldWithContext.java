package com.maciek.wojtaczka.encryption.framework;

import com.maciek.wojtaczka.encryption.framework.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;

import java.lang.reflect.Field;

class FieldWithContext {

	private static final Class<Encrypt> TO_ENCRYPT = Encrypt.class;


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

	Metadata getMetadata() {
		if (metadata == null) {
			Encrypt annotation = field.getDeclaredAnnotation(TO_ENCRYPT);
			this.metadata = Metadata.builder()
				.lazy(Boolean.parseBoolean(annotation.lazy()))
				.algorithm(annotation.algorithm())
				.build();
		}
		return metadata;
	}

	@Builder
	@Value
	static class Metadata {
		boolean lazy;
		String algorithm;
	}

}
