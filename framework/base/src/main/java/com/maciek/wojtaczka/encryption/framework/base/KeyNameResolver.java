package com.maciek.wojtaczka.encryption.framework.base;

public interface KeyNameResolver {

	String resolveEncryptionKeyName(Object entity);

	default String resolveBlindIdKeyName(Object entity) {
		return resolveEncryptionKeyName(entity);
	}

}
