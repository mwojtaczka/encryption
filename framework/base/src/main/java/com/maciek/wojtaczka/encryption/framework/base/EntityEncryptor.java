package com.maciek.wojtaczka.encryption.framework.base;

public interface EntityEncryptor <T> {

	void encryptObject(Object object);

	void encryptObject(Object object, String keyName);

	void decryptObject(Object object);

	void decryptObject(Object object, String keyName);
}
