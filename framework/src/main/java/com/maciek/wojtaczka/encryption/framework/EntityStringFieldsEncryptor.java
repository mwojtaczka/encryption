package com.maciek.wojtaczka.encryption.framework;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
			Object value = field.getValue();
			if (value instanceof String) {
				String encrypted = fieldEncryptor.encrypt((String) value, keyName, field.getAlgorithm());
				field.setValue(encrypted);
			} else if (value instanceof Iterable) {
				Iterable<String> iterable = (Iterable) value;

				Collection<String> collect = StreamSupport.stream(iterable.spliterator(), false)
					.map(s -> fieldEncryptor.encrypt(s, keyName, field.getAlgorithm()))
					.collect(Collectors.toCollection(getCollectionFactory(value)));

				field.setValue(collect);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private Supplier<Collection<String>> getCollectionFactory(Object value) {
		if (value instanceof List) {
			return ArrayList::new;
		} else if (value instanceof Set) {
			return HashSet::new;
		} else {
			throw new EncryptionException("Collection not supported");
		}
	}


	public void decryptObject(Object object, String keyName) {

		fieldExtractor.getAllFieldsToBeEncrypted(object, STRING_CLASS)
			.forEach(fieldWithContext -> decryptField(fieldWithContext, keyName));

	}

	private void decryptField(FieldWithContext field, String keyName) {

		try {
			Object value = field.getValue();
			if (value instanceof String) {
				String encrypted = fieldEncryptor.decrypt((String) value, keyName, field.getAlgorithm());
				field.setValue(encrypted);
			} else if (value instanceof Iterable) {
				Iterable<String> iterable = (Iterable) value;

				Collection<String> collect = StreamSupport.stream(iterable.spliterator(), false)
					.map(s -> fieldEncryptor.decrypt(s, keyName, field.getAlgorithm()))
					.collect(Collectors.toCollection(getCollectionFactory(value)));

				field.setValue(collect);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
