package com.maciek.wojtaczka.encryption.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

@Value(staticConstructor = "of")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class CipherRecord {

	private static final Charset CHARSET_FOR_BYTES_SERIALIZATION = ISO_8859_1;
	private static final String SEPARATOR = ":";
	private static final int BOOKMARKS_COUNT = 4;

	byte[] cipherContent;
	String cipherMechanismType;
	String encryptionKeyName;
	int encryptionKeyVersion;

	public static CipherRecord of(byte[] cipherResult, String cipherMechanismType, EncryptionKey encryptionKey) {
		return new CipherRecord(
			cipherResult,
			cipherMechanismType,
			encryptionKey.getName(),
			encryptionKey.getVersion());
	}

}
