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

public class EntityStringFieldsEncryptor extends AbstractLazyEntityEncryptor<String> {

	private static final Class<String> STRING_CLASS = String.class;

	private final FieldEncryptor<String> fieldEncryptor;
	private final FieldExtractor fieldExtractor;
	private final KeyNameResolver keyNameResolver;
	private final String hashingAlgorithm;

	public EntityStringFieldsEncryptor(FieldEncryptor<String> fieldEncryptor, KeyNameResolver keyNameResolver, String hashingAlgorithm) {
		this.fieldEncryptor = fieldEncryptor;
		this.fieldExtractor = new FieldExtractor();
		this.keyNameResolver = keyNameResolver;
		this.hashingAlgorithm = hashingAlgorithm;
	}

	@Override
	public void encryptObject(Object object) {

		String keyName = keyNameResolver.resolveEncryptionKeyName(object);
		String keyBlindIdName = keyNameResolver.resolveBlindIdKeyName();
		this.encryptObject(object, keyName, keyBlindIdName);
	}

	@Override
	public void encryptObject(Object object, String keyName) {

		fieldExtractor.getAllFieldsToBeEncrypted(object, STRING_CLASS)
					  .forEach(fieldWithContext -> processFieldEncryption(fieldWithContext, keyName, keyName));
	}

	@Override
	public void encryptObject(Object object, String keyName, String keyBlindId) {

		fieldExtractor.getAllFieldsToBeEncrypted(object, STRING_CLASS)
					  .forEach(fieldWithContext -> processFieldEncryption(fieldWithContext, keyName, keyBlindId));
	}


	private void processFieldEncryption(FieldWithContext field, String keyName, String keyBlindIdName) {

		FieldWithContext.Metadata fieldMetadata = field.getMetadata();
		try {
			Object value = field.getValue();
			if (value instanceof String) {
				if (field.isSearchable()) {
					String blindId = fieldEncryptor.hash((String) value, keyBlindIdName, hashingAlgorithm);
					field.setBlindId(blindId);
				}
				String encrypted = fieldEncryptor.encrypt((String) value, keyName, fieldMetadata.getAlgorithm());
				field.setValue(encrypted);
			} else if (value instanceof Iterable) {
				Iterable<String> iterable = (Iterable<String>) value;

				Collection<String> collect = StreamSupport.stream(iterable.spliterator(), false)
					.map(s -> fieldEncryptor.encrypt(s, keyName, fieldMetadata.getAlgorithm()))
					.collect(Collectors.toCollection(getCollectionFactory(value)));

				field.setValue(collect);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("BlindId field not found. Consider define field: " + fieldMetadata.getBlindIdFieldName());
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


	@Override
	public void decryptObject(Object object) {

		String keyName = keyNameResolver.resolveEncryptionKeyName(object);
		this.decryptObject(object, keyName);
	}

	@Override
	public void decryptObject(Object object, String keyName) {

		fieldExtractor.getAllFieldsToBeEncrypted(object, STRING_CLASS).stream()
			.filter(fieldWithContext -> !fieldWithContext.getMetadata().isLazy())
			.forEach(fieldWithContext -> decryptField(fieldWithContext, keyName));

	}

	private void decryptField(FieldWithContext field, String keyName) {

		try {
			Object value = field.getValue();
			if (value instanceof String) {
				String encrypted = fieldEncryptor.decrypt((String) value, keyName, field.getMetadata().getAlgorithm());
				field.setValue(encrypted);
			} else if (value instanceof List || value instanceof Set) {
				Iterable<String> iterable = (Iterable) value;

				Collection<String> collect = StreamSupport.stream(iterable.spliterator(), false)
					.map(s -> fieldEncryptor.decrypt(s, keyName, field.getMetadata().getAlgorithm()))
					.collect(Collectors.toCollection(getCollectionFactory(value)));

				field.setValue(collect);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private <T> T decryptFieldValue(FieldWithContext field, String keyName) {

		try {
			Object value = field.getValue();
			if (value instanceof String) {
				//noinspection unchecked
				return (T) fieldEncryptor.decrypt((String) value, keyName, field.getMetadata().getAlgorithm());
			} else if (value instanceof List || value instanceof Set) {
				Iterable<String> iterable = (Iterable) value; //check

				//noinspection unchecked
				return (T) StreamSupport.stream(iterable.spliterator(), false)
					.map(s -> fieldEncryptor.decrypt(s, keyName, field.getMetadata().getAlgorithm()))
					.collect(Collectors.toCollection(getCollectionFactory(value)));

			} else {
				throw new EncryptionException("Unsupported type for decryption");
			}
		} catch (IllegalAccessException e) {
			throw new EncryptionException("");
		}

	}

	@Override
	String decryptFieldLazily(Object entity, String fieldName,  String keyName) {

		FieldWithContext fieldByName;
		try {
			fieldByName = fieldExtractor.getFieldByName(entity, fieldName);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("No such field found", e);
		}

		return decryptFieldValue(fieldByName, keyName);
	}

	@Override
	List<String> decryptListFieldLazily(Object entity, String fieldName, String keyName) {
		FieldWithContext fieldByName;
		try {
			fieldByName = fieldExtractor.getFieldByName(entity, fieldName);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("No such field found", e);
		}

		return decryptFieldValue(fieldByName, keyName);
	}

	@Override
	Set<String> decryptSetFieldLazily(Object entity, String fieldName, String keyName) {
		FieldWithContext fieldByName;
		try {
			fieldByName = fieldExtractor.getFieldByName(entity, fieldName);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("No such field found", e);
		}

		return decryptFieldValue(fieldByName, keyName);
	}
}
