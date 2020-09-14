package com.maciek.wojtaczka;

import lombok.Value;

import javax.crypto.SecretKey;

@Value(staticConstructor = "of")
class EncryptionMaterials {

	private SecretKey secretKey;
	private byte[] iv;
}
