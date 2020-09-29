package com.maciek.wojtaczka.dummy.project.experimental;

import com.maciek.wojtaczka.encryption.core.AesGcmNoPaddingMechanism;
import com.maciek.wojtaczka.encryption.core.CipherMechanism;
import com.maciek.wojtaczka.encryption.core.EncryptionFacade;
import com.maciek.wojtaczka.encryption.core.EncryptionKey;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;
import com.maciek.wojtaczka.encryption.framework.base.EntityStringFieldsEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.FieldEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.KeyNameResolver;
import com.maciek.wojtaczka.encryption.framework.base.StringEncryptor;
import com.maciek.wojtaczka.encryption.framework.spring.aspect.EncryptionJpaAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Set;

@Configuration
public class EncryptionConfiguration {

	@Bean
	public EncryptionJpaAspect encryptionJpaAspect(EntityStringFieldsEncryptor encryptor) {
		return new EncryptionJpaAspect(encryptor);
	}

	@Bean
	public EntityStringFieldsEncryptor encryptor() {
		CipherMechanism cipherMechanism = new AesGcmNoPaddingMechanism();
		EncryptionKeyProvider encryptionKeyProvider = new StaticKeyProvider();
		EncryptionFacade encryptionFacade = new EncryptionFacade(Set.of(cipherMechanism), encryptionKeyProvider);
		FieldEncryptor<String> stringFieldEncryptor = new StringEncryptor(encryptionFacade);
		KeyNameResolver keyNameResolver = new StaticKeyNameResolver();
		return new EntityStringFieldsEncryptor(stringFieldEncryptor, keyNameResolver);
	}

	static class StaticKeyNameResolver implements KeyNameResolver {
		@Override
		public String resolveKeyName(Object entity) {
			return "encryption-key";
		}
	}

	static class StaticKeyProvider implements EncryptionKeyProvider {

		private final EncryptionKey encryptionKey;

		StaticKeyProvider() {
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



}
