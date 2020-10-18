package com.maciek.wojtaczka.encryption.core;

import lombok.Value;

import javax.crypto.SecretKey;

@Value(staticConstructor = "of")
public class EncryptionKey {

	String name;
	SecretKey secretKey;
	int version;

}
