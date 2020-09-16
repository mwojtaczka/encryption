package com.maciek.wojtaczka.encryption.framework;

import java.lang.reflect.Field;

class FieldWithContext {
	private final Field field;
	private final Object context;

	FieldWithContext(Field field, Object context) {
		this.field = field;
		this.context = context;
	}

	Object getValue() throws IllegalAccessException {
		this.field.setAccessible(true);
		return this.field.get(this.context);
	}
}
