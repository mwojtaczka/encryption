package com.maciek.wojtaczka.encryption.framework.base;

public abstract class AbstractLazyEntityEncryptor<F> implements EntityEncryptor<F> {

	public AbstractLazyEntityEncryptor() {
		StaticDecryptor.initialize(this);
	}

	abstract void decryptFieldLazily(Object entity, String fieldName, String keyName);

	abstract void decryptIterableFieldLazily(Object entity, String fieldName, String keyName);

}
