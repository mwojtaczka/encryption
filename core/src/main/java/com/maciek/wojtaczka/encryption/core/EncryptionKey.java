package com.maciek.wojtaczka.encryption.core;

import lombok.Value;

import javax.crypto.SecretKey;

@Value(staticConstructor = "of")
public class EncryptionKey {

	private String name;
	private SecretKey secretKey;
	private int version;

}
