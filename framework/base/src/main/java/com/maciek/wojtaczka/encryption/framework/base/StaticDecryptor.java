package com.maciek.wojtaczka.encryption.framework.base;

public final class StaticDecryptor {

	private static AbstractLazyEntityEncryptor<?> instance;

	public static synchronized <T> void initialize(AbstractLazyEntityEncryptor<T> entityEncryptor) {
		if (entityEncryptor != null) {
			instance = entityEncryptor;
		}
	}

	public static void decryptField(Object entity, String fieldName, String keyName) {
		instance.decryptFieldLazily(entity, fieldName, keyName);
	}

	public static void decryptIterableField(Object entity, String fieldName, String keyName) {
		instance.decryptIterableFieldLazily(entity, fieldName, keyName);
	}
}
