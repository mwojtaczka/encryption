package com.maciek.wojtaczka.encryption.core;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class InMemoryStaticKeyProvider implements EncryptionKeyProvider {

		private final EncryptionKey encryptionKey;

		public InMemoryStaticKeyProvider() {
			encryptionKey = EncryptionKey.of("static-key", generateAesSecretKey(), 1);
		}

		private SecretKey generateAesSecretKey() {
			SecureRandom secureRandom = new SecureRandom();
			byte[] key = new byte[16];
			secureRandom.nextBytes(key);
			return new SecretKeySpec(key, "AES");
		}

		@Override
		public EncryptionKey getLatestKey(String name, String algorithm) {
			return encryptionKey;
		}


		@Override
		public EncryptionKey getKey(String name, int version, String algorithm) {
			return encryptionKey;
		}
	}
