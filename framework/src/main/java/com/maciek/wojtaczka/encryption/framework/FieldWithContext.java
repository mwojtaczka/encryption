package com.maciek.wojtaczka.encryption.framework;

import lombok.Data;

import java.lang.reflect.Field;

@Data
class FieldWithContext {
	private final Field field;
	private final Object context;
	private final String algorithm;

	FieldWithContext(Field field, Object context, String algorithm) {
		this.field = field;
		this.context = context;
		this.algorithm = algorithm;
	}

	Object getValue() throws IllegalAccessException {
		this.field.setAccessible(true);
		return this.field.get(this.context);
	}

	void setValue(Object value) throws IllegalAccessException {
		this.field.setAccessible(true);
		this.field.set(this.context, value);
	}

	String getAlgorithm() {
		return "AES/GCM/NoPadding";
	}
}
