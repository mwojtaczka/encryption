package com.maciek.wojtaczka.encryption.core;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_16;

public class EncryptionFacade {

	private static final Charset CHARSET_FOR_CONTENT_SERIALIZATION = UTF_16;
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

	public String encryptString(String content, String keyName, String mechanismType) {

		CipherMechanism cipherMechanism = cipherMechanisms.get(mechanismType);
		if (cipherMechanism == null)
			throw new EncryptionException(mechanismType + " not found in the registry");

		EncryptionKey latestKey = keyProvider.getLatestKey(keyName);
		CipherResult cipherResult = cipherMechanism.encrypt(serializeContentString(content), latestKey.getSecretKey());

		return CipherRecord.of(cipherResult, latestKey).toStringRecord();
	}

	public String decryptString(String cipherContent, String keyName, String mechanismType) {

		CipherMechanism cipherMechanism = cipherMechanisms.get(mechanismType);
		if (cipherMechanism == null)
			throw new EncryptionException(mechanismType + " not found in the registry");


		CipherRecord cipherRecord = CipherRecord.of(cipherContent);

		EncryptionKey latestKey = keyProvider.getKey(keyName, cipherRecord.getEncryptionKeyVersion());
		EncryptionMaterials encryptionMaterials = EncryptionMaterials.of(latestKey.getSecretKey(), cipherRecord.getIv());
		byte[] decryptedContent = cipherMechanism.decrypt(cipherRecord.getCipherContent(), encryptionMaterials);

		return deserializeContentToString(decryptedContent);
	}


	private static String deserializeContentToString(byte[] contentBytes) {
		return new String(contentBytes, CHARSET_FOR_CONTENT_SERIALIZATION);
	}

	private static byte[] serializeContentString(String content) {
		return content.getBytes(CHARSET_FOR_CONTENT_SERIALIZATION);
	}

}
