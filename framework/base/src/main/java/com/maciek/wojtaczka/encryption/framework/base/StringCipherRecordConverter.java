package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.CipherRecord;

import java.nio.charset.Charset;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

class StringCipherRecordConverter {

	private static final Charset CHARSET_FOR_BYTES_SERIALIZATION = ISO_8859_1;
	private static final String SEPARATOR = ":";
	private static final int BOOKMARKS_COUNT = 4;

	String convertToString(CipherRecord cr) {
		int[] bookmarks = new int[BOOKMARKS_COUNT];
		String ivString = deserializeToString(cr.getIv());
		bookmarks[0] = countDigits(cr.getEncryptionKeyVersion());
		bookmarks[1] = cr.getEncryptionKeyName().length() + bookmarks[0];
		bookmarks[2] = cr.getCipherMechanismType().length() + bookmarks[1];
		bookmarks[3] = ivString.length() + bookmarks[2];

		String bookmarksString = IntStream.of(bookmarks)
										  .mapToObj(String::valueOf)
										  .collect(Collectors.joining(SEPARATOR, "", SEPARATOR));

		return bookmarksString +
				cr.getEncryptionKeyVersion() +
				cr.getEncryptionKeyName() +
				cr.getCipherMechanismType() +
				ivString +
				deserializeToString(cr.getCipherContent());
	}

	CipherRecord convertToCipherRecord(String stringRecord) {
		String[] split = stringRecord.split(SEPARATOR, BOOKMARKS_COUNT + 1);

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

	private byte[] serializeString(String stringFromBytes) {
		return stringFromBytes.getBytes(CHARSET_FOR_BYTES_SERIALIZATION);
	}

	private String deserializeToString(byte[] bytes) {
		return new String(bytes, CHARSET_FOR_BYTES_SERIALIZATION);
	}

	private int countDigits(int number) {
		int length = 0;
		long temp = 1;
		while (temp <= number) {
			length++;
			temp *= 10;
		}
		return length;
	}
}
