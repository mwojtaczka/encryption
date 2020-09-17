package com.maciek.wojtaczka.encryption.core;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EncryptionFacade {

	private final Map<String, CipherMechanism> cipherMechanisms;
	private final EncryptionKeyProvider keyProvider;

	public EncryptionFacade(Set<CipherMechanism> cipherMechanisms, EncryptionKeyProvider keyProvider) {
		this.cipherMechanisms = cipherMechanisms.stream()
			.collect(Collectors.toMap(
				CipherMechanism::getType,
				Function.identity()
			));
		this.keyProvider = keyProvider;
	}

	public CipherRecord encryptBytes(byte[] content, String keyName, String mechanismType) {

		CipherMechanism cipherMechanism = cipherMechanisms.get(mechanismType);
		if (cipherMechanism == null)
			throw new EncryptionException(mechanismType + " not found in the registry");

		EncryptionKey latestKey = keyProvider.getLatestKey(keyName, mechanismType);
		CipherResult cipherResult = cipherMechanism.encrypt(content, latestKey.getSecretKey());

		return CipherRecord.of(cipherResult, latestKey);
	}

	public byte[] decryptRecord(CipherRecord cipherRecord, String keyName, String mechanismType) {

		CipherMechanism cipherMechanism = cipherMechanisms.get(mechanismType);
		if (cipherMechanism == null)
			throw new EncryptionException(mechanismType + " not found in the registry");

		EncryptionKey latestKey = keyProvider.getKey(keyName, cipherRecord.getEncryptionKeyVersion(), mechanismType);
		EncryptionMaterials encryptionMaterials = EncryptionMaterials.of(latestKey.getSecretKey(), cipherRecord.getIv());

		return cipherMechanism.decrypt(cipherRecord.getCipherContent(), encryptionMaterials);
	}

}
