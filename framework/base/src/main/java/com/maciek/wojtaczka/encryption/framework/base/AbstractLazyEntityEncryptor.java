package com.maciek.wojtaczka.encryption.framework.base;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

public abstract class AbstractLazyEntityEncryptor<T> implements EntityEncryptor<T> {

	private final Class<T> actualType;

	public AbstractLazyEntityEncryptor() {
		actualType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		StaticDecryptor.initialize(this);
	}

	Class<T> getType() {
		return actualType;
	}

	public abstract void encryptObject(Object object, String keyName, String keyBlindId);

	abstract T decryptFieldLazily(Object entity, String fieldName, String keyName);

	abstract List<T> decryptListFieldLazily(Object entity, String fieldName, String keyName);

	abstract Set<T> decryptSetFieldLazily(Object entity, String fieldName, String keyName);

}
