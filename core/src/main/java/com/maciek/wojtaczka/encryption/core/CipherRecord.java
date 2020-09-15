package com.maciek.wojtaczka.encryption.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.charset.Charset;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class CipherRecord {

	private static final Charset CHARSET_FOR_BYTES_SERIALIZATION = ISO_8859_1;
	private static final String SEPARATOR = ":";
	private static final int BOOKMARKS_COUNT = 4;

	byte[] cipherContent;
	byte[] iv;
	String cipherMechanismType;
	String encryptionKeyName;
	int encryptionKeyVersion;

	static CipherRecord of(CipherResult cipherResult, EncryptionKey encryptionKey) {
		return new CipherRecord(
			cipherResult.getCipherContent(),
			cipherResult.getIv(),
			cipherResult.getCipherMechanism(),
			encryptionKey.getName(),
			encryptionKey.getVersion());
	}

	static CipherRecord of(String cipherRecord) {
		String[] split = cipherRecord.split(SEPARATOR, BOOKMARKS_COUNT + 1);

		int[] bookmarks = Stream.of(split)
			.mapToInt(Integer::parseInt)
			.limit(BOOKMARKS_COUNT)
			.toArray();

		String cipherRecordWithNoBookmarks = split[BOOKMARKS_COUNT];

		int encryptionKeyVersion = Integer.parseInt(cipherRecordWithNoBookmarks.substring(0, bookmarks[0]));
		String encryptionKeyName = cipherRecordWithNoBookmarks.substring(bookmarks[0], bookmarks[1]);
		String cipherMechanismType = cipherRecordWithNoBookmarks.substring(bookmarks[1], bookmarks[2]);
		String iv = cipherRecordWithNoBookmarks.substring(bookmarks[2], bookmarks[3]);
		String cipherContent = cipherRecordWithNoBookmarks.substring(bookmarks[3]);

		return new CipherRecord(
			serializeString(cipherContent),
			serializeString(iv),
			cipherMechanismType,
			encryptionKeyName,
			encryptionKeyVersion
		);
	}

	private static byte[] serializeString(String stringFromBytes) {
		return stringFromBytes.getBytes(CHARSET_FOR_BYTES_SERIALIZATION);
	}

	private static String deserializeToString(byte[] bytes) {
		return new String(bytes, CHARSET_FOR_BYTES_SERIALIZATION);
	}

	String toStringRecord() {
		int[] bookmarks = new int[BOOKMARKS_COUNT];
		String ivString = deserializeToString(iv);
		bookmarks[0] = (encryptionKeyVersion / 10) + 1;
		bookmarks[1] = encryptionKeyName.length() + bookmarks[0];
		bookmarks[2] = cipherMechanismType.length() + bookmarks[1];
		bookmarks[3] = ivString.length() + bookmarks[2];

		String bookmarksString = IntStream.of(bookmarks)
			.mapToObj(String::valueOf)
			.collect(Collectors.joining(SEPARATOR, "", SEPARATOR));

		return bookmarksString +
			encryptionKeyVersion +
			encryptionKeyName +
			cipherMechanismType +
			ivString +
			deserializeToString(cipherContent);
	}

}
