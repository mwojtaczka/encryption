package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;

import java.util.List;
import java.util.Set;

public final class StaticDecryptor {

	private static AbstractLazyEntityEncryptor<?> instance;

	public static synchronized <T> void initialize(AbstractLazyEntityEncryptor<T> entityEncryptor) {
		if (entityEncryptor != null) {
			instance = entityEncryptor;
		}
	}

	public static <T> T decryptField(Object entity, String fieldName, String keyName) {
		Object decryptedField = instance.decryptFieldLazily(entity, fieldName, keyName);
		if (instance.getType().isInstance(decryptedField)) { //TODO: check impact of the check and consider removal (this check will never fail)
			return (T) decryptedField;
		} else {
			throw new EncryptionException("The field type is not compliant to the typed passed during initialization which is: " +
												  instance.getType().getCanonicalName());
		}
	}

	public static <T> List<T> decryptListField(Object entity, String fieldName, String keyName) {
		List<?> decryptedField = instance.decryptListFieldLazily(entity, fieldName, keyName);
		if (!decryptedField.isEmpty() && instance.getType().isInstance(decryptedField.get(0))) { //TODO: check impact of the check and consider removal (this check will never fail)
			return (List<T>) decryptedField;
		} else {
			throw new EncryptionException("The field type is not compliant to the typed passed during initialization which is: " +
												  instance.getType().getCanonicalName());
		}
	}

	public static <T> Set<T> decryptSetField(Object entity, String fieldName, String keyName) {
		Set<?> decryptedField = instance.decryptSetFieldLazily(entity, fieldName, keyName);
		if (!decryptedField.isEmpty() && isElementOfType(decryptedField, instance.getType())) { //TODO: check impact of the check and consider removal (this check will never fail)
			return (Set<T>) decryptedField;
		} else {
			throw new EncryptionException("The field type is not compliant to the typed passed during initialization which is: " +
												  instance.getType().getCanonicalName());
		}
	}

	private static boolean isElementOfType(Iterable<?> iterable, Class<?> aClass) {
		Object next = iterable.iterator()
							  .next();
		return aClass.isInstance(next);
	}
}
