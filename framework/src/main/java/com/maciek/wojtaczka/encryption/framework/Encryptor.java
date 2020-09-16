package com.maciek.wojtaczka.encryption.framework;

import com.maciek.wojtaczka.encryption.core.EncryptionFacade;

public class Encryptor {

	private final EncryptionFacade encryptionFacade;
	private final FieldExtractor fieldExtractor;

	public Encryptor(EncryptionFacade encryptionFacade, FieldExtractor fieldExtractor) {
		this.encryptionFacade = encryptionFacade;
		this.fieldExtractor = fieldExtractor;
	}

	public void encryptObject(Object object, String keyName) {

		fieldExtractor.getAllFieldsToBeEncrypted(object)
		.forEach(this::encryptField);

	}

	private void encryptField(FieldWithContext field) {

	}
}
