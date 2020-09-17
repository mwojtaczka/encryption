package com.maciek.wojtaczka.encryption.core;

public interface EncryptionKeyProvider {

	EncryptionKey getLatestKey(String name, String algorithm);

	EncryptionKey getKey(String name, int version, String algorithm);
}
