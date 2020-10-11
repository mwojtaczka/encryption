package com.maciek.wojtaczka.encryption.framework.base;

public interface FieldEncryptor <C> {

	C encrypt(C c, String keyName, String algorithm);

	C decrypt(C c, String keyName, String algorithm);

	String hash(C c, String keyName, String algorithm);
}
