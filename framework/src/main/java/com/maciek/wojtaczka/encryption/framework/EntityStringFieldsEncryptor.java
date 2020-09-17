package com.maciek.wojtaczka.encryption.framework;

public class EntityStringFieldsEncryptor {

	private static final Class<String> STRING_CLASS = String.class;

	private final FieldEncryptor<String> fieldEncryptor;
	private final FieldExtractor fieldExtractor;

	public EntityStringFieldsEncryptor(FieldEncryptor<String> fieldEncryptor, FieldExtractor fieldExtractor) {
		this.fieldEncryptor = fieldEncryptor;
		this.fieldExtractor = fieldExtractor;
	}

	public void encryptObject(Object object, String keyName) {

		fieldExtractor.getAllFieldsToBeEncrypted(object, STRING_CLASS)
			.forEach(fieldWithContext -> encryptField(fieldWithContext, keyName));

	}

	private void encryptField(FieldWithContext field, String keyName) {

		try {
			String value = (String) field.getValue();
			String encrypted = fieldEncryptor.encrypt(value, keyName, field.getAlgorithm());
			field.setValue(encrypted);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
