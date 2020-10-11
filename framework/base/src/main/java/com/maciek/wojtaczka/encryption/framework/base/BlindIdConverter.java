package com.maciek.wojtaczka.encryption.framework.base;

public class BlindIdConverter <C> {

	private final FieldEncryptor<C> fieldEncryptor;
	private final KeyNameResolver keyNameResolver;
	private final String hashingAlgorithm;

	public BlindIdConverter(FieldEncryptor<C> fieldEncryptor, KeyNameResolver keyNameResolver, String hashingAlgorithm) {
		this.fieldEncryptor = fieldEncryptor;
		this.keyNameResolver = keyNameResolver;
		this.hashingAlgorithm = hashingAlgorithm;
	}

	public String hash(C value) {
		return fieldEncryptor.hash(value, keyNameResolver.resolveBlindIdKeyName(), hashingAlgorithm);
	}
}
