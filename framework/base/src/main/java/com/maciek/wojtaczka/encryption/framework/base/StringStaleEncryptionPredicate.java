package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.CipherRecord;
import com.maciek.wojtaczka.encryption.core.EncryptionKey;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;

import static java.util.stream.StreamSupport.stream;

public class StringStaleEncryptionPredicate implements StaleEncryptionPredicate {

	private final FieldExtractor fieldExtractor;
	private final StringCipherRecordConverter converter;
	private final EncryptionKeyProvider encryptionKeyProvider;

	public StringStaleEncryptionPredicate(EncryptionKeyProvider encryptionKeyProvider) {
		this.encryptionKeyProvider = encryptionKeyProvider;
		fieldExtractor = new FieldExtractor();
		converter = new StringCipherRecordConverter();
	}

	@Override
	public boolean isStale(Object entity) {

		FieldExtractor.FieldsContainer<String> toBeEncryptedContainer = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class);
		boolean isAnyFieldStale = toBeEncryptedContainer
								   .getFields()
								   .stream()
								   .map(FieldWithContext::getValue)
								   .map(converter::convertToCipherRecord)
								   .anyMatch(this::isCipherRecordKeyVersionLowerThenLatestKey);

		boolean isAnyIterableFieldStale = toBeEncryptedContainer
								   .getIterableFields()
								   .stream()
								   .flatMap(iterableFieldWithContext -> stream(iterableFieldWithContext.getValue().spliterator(), false))
								   .map(converter::convertToCipherRecord)
								   .anyMatch(this::isCipherRecordKeyVersionLowerThenLatestKey);

		return isAnyFieldStale || isAnyIterableFieldStale;
	}

	private boolean isCipherRecordKeyVersionLowerThenLatestKey(CipherRecord cipherRecord) {

		String cipherMechanismType = cipherRecord.getCipherMechanismType();
		String encryptionKeyName = cipherRecord.getEncryptionKeyName();
		EncryptionKey latestKey = encryptionKeyProvider.getLatestKey(encryptionKeyName, cipherMechanismType);

		return cipherRecord.getEncryptionKeyVersion() < latestKey.getVersion();
	}
}
