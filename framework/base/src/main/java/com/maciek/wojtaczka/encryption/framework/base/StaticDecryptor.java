package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;

import java.util.List;
import java.util.Set;

public final class StaticDecryptor {

	private static AbstractLazyEntityEncryptor<?> instance;
	private static Class<?> clazz;

	public static synchronized <T> void initialize(AbstractLazyEntityEncryptor<T> entityEncryptor, Class<T> fieldClass) {
		if (entityEncryptor != null) {
			instance = entityEncryptor;
			clazz = fieldClass;
		}
	}

	public static <T> T decryptField(Object entity, String fieldName, String keyName) {
		Object decryptedField = instance.decryptFieldLazily(entity, fieldName, keyName);
		if (clazz.isInstance(decryptedField)) { //TODO: check impact of the check and consider removal (this check will never fail)
			return (T) decryptedField;
		} else {
			throw new EncryptionException("The field type is not compliant to the typed passed during initialization which is: " +
												  clazz.getCanonicalName());
		}
	}

	public static <T> List<T> decryptListField(Object entity, String fieldName, String keyName) {
		List<?> decryptedField = instance.decryptListFieldLazily(entity, fieldName, keyName);
		if (!decryptedField.isEmpty() && clazz.isInstance(decryptedField.get(0))) { //TODO: check impact of the check and consider removal (this check will never fail)
			return (List<T>) decryptedField;
		} else {
			throw new EncryptionException("The field type is not compliant to the typed passed during initialization which is: " +
												  clazz.getCanonicalName());
		}
	}

	public static <T> Set<T> decryptSetField(Object entity, String fieldName, String keyName) {
		Set<?> decryptedField = instance.decryptSetFieldLazily(entity, fieldName, keyName);
		if (!decryptedField.isEmpty() && isElementOfType(decryptedField, clazz)) { //TODO: check impact of the check and consider removal (this check will never fail)
			return (Set<T>) decryptedField;
		} else {
			throw new EncryptionException("The field type is not compliant to the typed passed during initialization which is: " +
												  clazz.getCanonicalName());
		}
	}

	private static boolean isElementOfType(Iterable<?> iterable, Class<?> aClass) {
		Object next = iterable.iterator()
							  .next();
		return aClass.isInstance(next);
	}
}
