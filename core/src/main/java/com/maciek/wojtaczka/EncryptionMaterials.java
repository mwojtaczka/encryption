package com.maciek.wojtaczka;

import lombok.Value;

import javax.crypto.SecretKey;

@Value
public class EncryptionMaterials {

	private SecretKey secretKey;
	private byte[] iv;
}
