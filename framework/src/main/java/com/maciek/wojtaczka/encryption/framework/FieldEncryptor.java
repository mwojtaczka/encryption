package com.maciek.wojtaczka.encryption.framework;

public interface FieldEncryptor <C> {

	C encrypt(C c, String keyName, String algorithm);

	C decrypt(C c, String keyName, String algorithm);
}