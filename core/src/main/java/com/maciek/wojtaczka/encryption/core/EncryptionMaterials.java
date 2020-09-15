package com.maciek.wojtaczka.encryption.core;

import lombok.Value;

import javax.crypto.SecretKey;

@Value(staticConstructor = "of")
class EncryptionMaterials {

	private SecretKey secretKey;
	private byte[] iv;
}
