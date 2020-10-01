package com.maciek.wojtaczka.encryption.framework.base;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

public abstract class AbstractLazyEntityEncryptor<T> implements EntityEncryptor<T> {

	public AbstractLazyEntityEncryptor() {
		Class<T> actualTypeArgument = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		StaticDecryptor.initialize(this, actualTypeArgument);
	}

	<E> Class<E> getType() {
		return null;
	}

	abstract T decryptFieldLazily(Object entity, String fieldName, String keyName);

	abstract List<T> decryptListFieldLazily(Object entity, String fieldName, String keyName);

	abstract Set<T> decryptSetFieldLazily(Object entity, String fieldName, String keyName);

}
