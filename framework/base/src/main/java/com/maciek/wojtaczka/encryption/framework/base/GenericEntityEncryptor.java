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

public class GenericEntityEncryptor<F> extends AbstractLazyEntityEncryptor<F> {

	private final Class<F> encryptedFieldType;

	private final FieldEncryptor<F> fieldEncryptor;
	private final FieldExtractor fieldExtractor;
	private final KeyNameResolver keyNameResolver;
	private final String hashingAlgorithm;

	public GenericEntityEncryptor(FieldEncryptor<F> fieldEncryptor, KeyNameResolver keyNameResolver, String hashingAlgorithm,
								  Class<F> encryptedFieldType) {
		this.fieldEncryptor = fieldEncryptor;
		this.fieldExtractor = new FieldExtractor();
		this.keyNameResolver = keyNameResolver;
		this.hashingAlgorithm = hashingAlgorithm;
		this.encryptedFieldType = encryptedFieldType;
	}

	@Override
	public void encryptObject(Object object) {

		String keyName = keyNameResolver.resolveEncryptionKeyName(object);
		this.encryptObject(object, keyName);
	}

	@Override
	public void encryptObject(Object object, String keyName) {

		String keyBlindIdName = keyNameResolver.resolveBlindIdKeyName();
		FieldExtractor.FieldsContainer<F> fieldsToBeEncryptedContainer = fieldExtractor.getAllFieldsToBeEncrypted(object, encryptedFieldType);
		fieldsToBeEncryptedContainer
				.getFields()
				.forEach(fieldWithContext -> encryptField(fieldWithContext, keyName, keyBlindIdName));
		fieldsToBeEncryptedContainer
				.getIterableFields()
				.forEach(fieldWithContext -> encryptIterableField(fieldWithContext, keyName));
	}


	private void encryptField(FieldWithContext<F> field, String keyName, String keyBlindIdName) {

		FieldWithContext.Metadata fieldMetadata = field.getMetadata();
		try {
			F value = field.getValue();
			if (field.isSearchable()) {
				String blindId = fieldEncryptor.hash(value, keyBlindIdName, hashingAlgorithm);
				field.setBlindId(blindId);
			}
			F encrypted = fieldEncryptor.encrypt(value, keyName, fieldMetadata.getAlgorithm());
			field.setValue(encrypted);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("BlindId field not found. Consider define field: " + fieldMetadata.getBlindIdFieldName());
		}
	}

	private void encryptIterableField(FieldWithContext<Iterable<F>> field, String keyName) {

		FieldWithContext.Metadata fieldMetadata = field.getMetadata();
		Iterable<F> iterable = field.getValue();

		Collection<F> collect = StreamSupport.stream(iterable.spliterator(), false)
											 .map(s -> fieldEncryptor.encrypt(s, keyName, fieldMetadata.getAlgorithm()))
											 .collect(Collectors.toCollection(getCollectionFactory(iterable)));

		field.setValue(collect);
	}

	private Supplier<Collection<F>> getCollectionFactory(Iterable<F> value) {
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

		FieldExtractor.FieldsContainer<F> fieldsToBeEncryptedContainer = fieldExtractor.getAllFieldsToBeEncrypted(object, encryptedFieldType);
		fieldsToBeEncryptedContainer
				.getFields()
				.stream()
				.filter(fieldWithContext -> !fieldWithContext.getMetadata().isLazy())
				.forEach(fieldWithContext -> decryptField(fieldWithContext, keyName));
		fieldsToBeEncryptedContainer
				.getIterableFields()
				.stream()
				.filter(fieldWithContext -> !fieldWithContext.getMetadata().isLazy())
				.forEach(fieldWithContext -> decryptIterableField(fieldWithContext, keyName));
	}

	private void decryptField(FieldWithContext<F> field, String keyName) {

		F value = field.getValue();
		if (value == null) {
			return;
		}

		FieldWithContext.Metadata fieldMetadata = field.getMetadata();
		F encrypted = fieldEncryptor.decrypt(value, keyName, fieldMetadata.getAlgorithm());
		field.setValue(encrypted);
	}

	private void decryptIterableField(FieldWithContext<Iterable<F>> field, String keyName) {

		Iterable<F> iterable = field.getValue();
		if (iterable == null) {
			return;
		}

		Collection<F> collect = StreamSupport.stream(iterable.spliterator(), false)
											 .map(s -> fieldEncryptor.decrypt(s, keyName, field.getMetadata().getAlgorithm()))
											 .collect(Collectors.toCollection(getCollectionFactory(iterable)));

		field.setValue(collect);
	}

	//LAZY

	@Override
	void decryptFieldLazily(Object entity, String fieldName, String keyName) {
		FieldWithContext<F> fieldByName;
		try {
			fieldByName = fieldExtractor.getFieldByName(entity, fieldName, encryptedFieldType);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("No such field found", e);
		}

		decryptField(fieldByName, keyName);
	}

	@Override
	void decryptIterableFieldLazily(Object entity, String fieldName, String keyName) {
		FieldWithContext<Iterable<F>> fieldByName;
		try {
			fieldByName = fieldExtractor.getIterableFieldByName(entity, fieldName, encryptedFieldType);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("No such field found", e);
		}

		decryptIterableField(fieldByName, keyName);
	}
}
