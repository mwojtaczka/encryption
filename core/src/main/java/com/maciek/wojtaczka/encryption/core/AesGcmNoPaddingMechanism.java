package com.maciek.wojtaczka.encryption.core;

import com.maciek.wojtaczka.encryption.core.exception.EncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AesGcmNoPaddingMechanism implements CipherMechanism {

	private static final String ENCRYPTION_MECHANISM = "AES/GCM/NoPadding";
	private static final int GCM_IV_SIZE = 12;
	private static final int AUTH_TAG_SIZE = 128;

	@Override
	public String getType() {
		return ENCRYPTION_MECHANISM;
	}

	@Override
	public CipherResult encrypt(byte[] content, SecretKey secretKey) {
		try {
			byte[] iv = generateInitialVector();
			byte[] cipherContent = cipher(content, iv, secretKey);

			return CipherResult.of(cipherContent, iv, ENCRYPTION_MECHANISM);

		} catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException |
			NoSuchPaddingException | IllegalBlockSizeException e) {
			throw new EncryptionException(e.getMessage(), e);
		}
	}

	private byte[] cipher(byte[] plainValue, byte[] iv, SecretKey secretKey) throws NoSuchAlgorithmException,
		NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
		BadPaddingException {
		GCMParameterSpec parameterSpec = new GCMParameterSpec(AUTH_TAG_SIZE, iv);
		Cipher cipher = Cipher.getInstance(ENCRYPTION_MECHANISM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
		return cipher.doFinal(plainValue);
	}

	private byte[] generateInitialVector() {
		byte[] iv = new byte[GCM_IV_SIZE];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(iv);
		return iv;
	}

	@Override
	public byte[] decrypt(byte[] cipherContent, EncryptionMaterials encryptionMaterials) {
		try {
			byte[] iv = encryptionMaterials.getIv();
			verifyIvLength(iv);
			SecretKey secretKey = encryptionMaterials.getSecretKey();

			return decipher(cipherContent, iv, secretKey);

		} catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException |
			NoSuchPaddingException | IllegalBlockSizeException e) {
			throw new EncryptionException(e.getMessage(), e);
		}

	}

	private void verifyIvLength(byte[] iv) {
		if (iv.length != GCM_IV_SIZE) {
			throw new EncryptionException("Invalid IV length for " + ENCRYPTION_MECHANISM + " mechanism. " +
				"Should be: " + GCM_IV_SIZE + " but is: " + iv.length);
		}
	}

	private byte[] decipher(byte[] cipherText, byte[] iv, SecretKey secretKey) throws NoSuchAlgorithmException,
		NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
		BadPaddingException {
		Cipher cipher = Cipher.getInstance(ENCRYPTION_MECHANISM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(AUTH_TAG_SIZE, iv));
		return cipher.doFinal(cipherText);
	}
}
