package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.CipherRecord;
import com.maciek.wojtaczka.encryption.core.EncryptionKey;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;

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

		boolean b = fieldExtractor.getAllFieldsToBeEncrypted(entity, String.class)
								  .stream()
								  .map(FieldWithContext::getValue)
								  .map(o -> (String) o)
								  .map(converter::convertToCipherRecord)
								  .anyMatch(this::isCipherRecordKeyVersionLowerThenLatestKey);
		return b;
	}

	private boolean isCipherRecordKeyVersionLowerThenLatestKey(CipherRecord cipherRecord) {

		String cipherMechanismType = cipherRecord.getCipherMechanismType();
		String encryptionKeyName = cipherRecord.getEncryptionKeyName();
		EncryptionKey latestKey = encryptionKeyProvider.getLatestKey(encryptionKeyName, cipherMechanismType);

		return cipherRecord.getEncryptionKeyVersion() < latestKey.getVersion();
	}
}
