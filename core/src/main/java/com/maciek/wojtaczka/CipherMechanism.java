package com.maciek.wojtaczka;

import javax.crypto.SecretKey;

public interface CipherMechanism {

	CipherResult encrypt(byte[] content, SecretKey secretKey);

	byte[] decrypt(byte[] cipherContent, EncryptionMaterials encryptionMaterials);

}
