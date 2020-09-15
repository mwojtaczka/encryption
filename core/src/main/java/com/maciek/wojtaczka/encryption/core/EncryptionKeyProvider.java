package com.maciek.wojtaczka.encryption.core;

public interface EncryptionKeyProvider {

	EncryptionKey getLatestKey(String name);

	EncryptionKey getKey(String name, int version);
}
