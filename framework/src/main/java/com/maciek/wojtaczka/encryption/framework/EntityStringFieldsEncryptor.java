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
	private static EntityStringFieldsEncryptor instance;

	private final FieldEncryptor<String> fieldEncryptor;
	private final FieldExtractor fieldExtractor;
	private final KeyNameResolver keyNameResolver;

	EntityStringFieldsEncryptor createAndInitializeStaticReference(FieldEncryptor<String> fieldEncryptor, FieldExtractor fieldExtractor) {
		EntityStringFieldsEncryptor encryptor = new EntityStringFieldsEncryptor(fieldEncryptor, fieldExtractor, keyNameResolver);
		instance = encryptor;
		return encryptor;
	}

	public EntityStringFieldsEncryptor(FieldEncryptor<String> fieldEncryptor, FieldExtractor fieldExtractor, KeyNameResolver keyNameResolver) {
		this.fieldEncryptor = fieldEncryptor;
		this.fieldExtractor = fieldExtractor;
		this.keyNameResolver = keyNameResolver;
		instance = this;
	}

	public void encryptObject(Object object) {

		String keyName = keyNameResolver.resolveKeyName(object);
		this.encryptObject(object, keyName);
	}

	public void encryptObject(Object object, String keyName) {

		fieldExtractor.getAllFieldsToBeEncrypted(object, STRING_CLASS)
			.forEach(fieldWithContext -> encryptField(fieldWithContext, keyName));

	}

	private void encryptField(FieldWithContext field, String keyName) {

		try {
			Object value = field.getValue();
			if (value instanceof String) {
				String encrypted = fieldEncryptor.encrypt((String) value, keyName, field.getMetadata().getAlgorithm());
				field.setValue(encrypted);
			} else if (value instanceof Iterable) {
				Iterable<String> iterable = (Iterable) value;

				Collection<String> collect = StreamSupport.stream(iterable.spliterator(), false)
					.map(s -> fieldEncryptor.encrypt(s, keyName, field.getMetadata().getAlgorithm()))
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


	public void decryptObject(Object object) {

		String keyName = keyNameResolver.resolveKeyName(object);
		this.decryptObject(object, keyName);
	}

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

	public static  <T> T decryptLazily(Object t, String fieldName,  String keyName) {
		if (instance == null) {
			throw new EncryptionException("Encryptor has not been initialized. Consider create at lest one instance of EntityStringFieldsEncryptor class");
		}
		FieldWithContext fieldByName;
		try {
			fieldByName = instance.fieldExtractor.getFieldByName(t, fieldName);
		} catch (NoSuchFieldException e) {
			throw new EncryptionException("No such field found", e);
		}

		return instance.decryptFieldValue(fieldByName, keyName);
	}
}
