package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.CipherRecord;
import com.maciek.wojtaczka.encryption.core.EncryptionFacade;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_16;

public class StringEncryptor implements FieldEncryptor<String> {

	private static final Charset CHARSET_FOR_CONTENT_SERIALIZATION = UTF_16;


	private final EncryptionFacade encryptionFacade;
	private final StringCipherRecordConverter converter;

	public StringEncryptor(EncryptionFacade encryptionFacade) {
		this.encryptionFacade = encryptionFacade;
		this.converter = new StringCipherRecordConverter();
	}

	@Override
	public String encrypt(String content, String keyName, String algorithm) {

		byte[] bytes = serializeContentString(content);
		CipherRecord cipherRecord = encryptionFacade.encryptBytes(bytes, keyName, algorithm);

 		return converter.convertToString(cipherRecord);
	}

	@Override
	public String decrypt(String encryptedContent, String keyName, String algorithm) {

		CipherRecord cipherRecord = converter.convertToCipherRecord(encryptedContent);
		byte[] bytes = encryptionFacade.decryptRecord(cipherRecord, keyName, algorithm);

		return deserializeContentToString(bytes);
	}

	@Override
	public String hash(String content, String keyName, String algorithm) {
		byte[] bytes = serializeContentString(content);
		CipherRecord cipherRecord = encryptionFacade.encryptBytes(bytes, keyName, algorithm);

		return deserializeContentToString(cipherRecord.getCipherContent());
	}

	private String deserializeContentToString(byte[] contentBytes) {
		return new String(contentBytes, CHARSET_FOR_CONTENT_SERIALIZATION);
	}

	private byte[] serializeContentString(String content) {
		return content.getBytes(CHARSET_FOR_CONTENT_SERIALIZATION);
	}


}
