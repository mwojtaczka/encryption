package com.maciek.wojtaczka.encryption.framework.base;

public interface KeyNameResolver {

	String resolveEncryptionKeyName(Object entity);

	String resolveBlindIdKeyName();

}
