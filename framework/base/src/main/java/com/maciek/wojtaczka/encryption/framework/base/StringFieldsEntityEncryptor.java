package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StringFieldsEntityEncryptor extends AbstractLazyEntityEncryptor<String> {

	private static final Class<String> STRING_CLASS = String.class;

	private final FieldEncryptor<String> fieldEncryptor;
	private final FieldExtractor fieldExtractor;
	private final KeyNameResolver keyNameResolver;
	private final String hashingAlgorithm;

	public StringFieldsEntityEncryptor(FieldEncryptor<String> fieldEncryptor, KeyNameResolver keyNameResolver, String hashingAlgorithm) {
		this.fieldEncryptor = fieldEncryptor;
		this.fieldExtractor = new FieldExtractor();
		this.keyNameResolver = keyNameResolver;
		this.hashingAlgorithm = hashingAlgorithm;
	}

	@Override
	public void encryptObject(Object object) {

		String keyName = keyNameResolver.resolveEncryptionKeyName(object);
		this.encryptObject(object, keyName);
	}

	@Override
	public void encryptObject(Object object, String keyName) {

		String keyBlindIdName = keyNameResolver.resolveBlindIdKeyName();
		FieldExtractor.FieldsContainer<String> fieldsToBeEncryptedContainer = fieldExtractor.getAllFieldsToBeEncrypted(object, STRING_CLASS);
		fieldsToBeEncryptedContainer
				.getFields()
				.forEach(fieldWithContext -> encryptField(fieldWithContext, keyName, keyBlindIdName));
		fieldsToBeEncryptedContainer
				.getIterableFields()
				.forEach(fieldWithContext -> encryptIterableField(fieldWithContext, keyName));
	}


	private void encryptField(FieldWithContext<String> field, String keyName, String keyBlindIdName) {

		FieldWithContext.Metadata fieldMetadata = field.getMetadata();
		try {
			String value = field.getValue();
			if (field.isSearchable()) {
				String blindId = fieldEncryptor.hash(value, keyBlindIdName, hashingAlgorithm);
				field.setBlindId(blindId);
			}
			String encrypted = fieldEncryptor.encrypt(value, keyName, fieldMetadata.getAlgorithm());
			field.setValue(encrypted);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("BlindId field not found. Consider define field: " + fieldMetadata.getBlindIdFieldName());
		}
	}

	private void encryptIterableField(FieldWithContext<Iterable<String>> field, String keyName) {

		FieldWithContext.Metadata fieldMetadata = field.getMetadata();
		Iterable<String> iterable = field.getValue();

		Collection<String> collect = StreamSupport.stream(iterable.spliterator(), false)
												  .map(s -> fieldEncryptor.encrypt(s, keyName, fieldMetadata.getAlgorithm()))
												  .collect(Collectors.toCollection(getCollectionFactory(iterable)));

		field.setValue(collect);
	}

	private Supplier<Collection<String>> getCollectionFactory(Iterable<String> value) {
		if (value instanceof List) {
			return ArrayList::new;
		} else if (value instanceof Set) {
			return HashSet::new;
		} else {
			throw new EncryptionException("Collection type: " + value.getClass() + " not supported");
		}
	}


	@Override
	public void decryptObject(Object object) {

		String keyName = keyNameResolver.resolveEncryptionKeyName(object);
		this.decryptObject(object, keyName);
	}

	@Override
	public void decryptObject(Object object, String keyName) {

		FieldExtractor.FieldsContainer<String> fieldsToBeEncryptedContainer = fieldExtractor.getAllFieldsToBeEncrypted(object, STRING_CLASS);
		fieldsToBeEncryptedContainer
				.getFields()
				.stream()
				.filter(fieldWithContext -> !fieldWithContext.getMetadata()
															 .isLazy())
				.forEach(fieldWithContext -> decryptField(fieldWithContext, keyName));
		fieldsToBeEncryptedContainer
				.getIterableFields()
				.stream()
				.filter(fieldWithContext -> !fieldWithContext.getMetadata()
															 .isLazy())
				.forEach(fieldWithContext -> decryptIterableField(fieldWithContext, keyName));
	}

	private void decryptField(FieldWithContext<String> field, String keyName) {

		String value = field.getValue();
		if (value == null) {
			return;
		}

		String encrypted = fieldEncryptor.decrypt(value, keyName, field.getMetadata()
																	   .getAlgorithm());
		field.setValue(encrypted);
	}

	private void decryptIterableField(FieldWithContext<Iterable<String>> field, String keyName) {

		Iterable<String> iterable = field.getValue();
		if (iterable == null) {
			return;
		}

		Collection<String> collect = StreamSupport.stream(iterable.spliterator(), false)
												  .map(s -> fieldEncryptor.decrypt(s, keyName, field.getMetadata()
																									.getAlgorithm()))
												  .collect(Collectors.toCollection(getCollectionFactory(iterable)));

		field.setValue(collect);
	}

	//LAZY

	@Override
	void decryptFieldLazily(Object entity, String fieldName, String keyName) {
		FieldWithContext<String> fieldByName;
		try {
			fieldByName = fieldExtractor.getFieldByName(entity, fieldName, String.class);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("No such field found", e);
		}

		decryptField(fieldByName, keyName);
	}

	@Override
	void decryptIterableFieldLazily(Object entity, String fieldName, String keyName) {
		FieldWithContext<Iterable<String>> fieldByName;
		try {
			fieldByName = fieldExtractor.getIterableFieldByName(entity, fieldName, String.class);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("No such field found", e);
		}

		decryptIterableField(fieldByName, keyName);
	}
}
