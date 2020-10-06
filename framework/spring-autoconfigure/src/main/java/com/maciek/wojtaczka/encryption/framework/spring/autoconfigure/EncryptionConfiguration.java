package com.maciek.wojtaczka.encryption.framework.spring.autoconfigure;

import com.maciek.wojtaczka.encryption.core.AesGcmNoPaddingMechanism;
import com.maciek.wojtaczka.encryption.core.CipherMechanism;
import com.maciek.wojtaczka.encryption.core.EncryptionFacade;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;
import com.maciek.wojtaczka.encryption.core.HmacSha256Mechanism;
import com.maciek.wojtaczka.encryption.core.InMemoryStaticKeyProvider;
import com.maciek.wojtaczka.encryption.framework.base.BlindIdConverter;
import com.maciek.wojtaczka.encryption.framework.base.EntityEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.EntityStringFieldsEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.FieldEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.KeyNameResolver;
import com.maciek.wojtaczka.encryption.framework.base.StaticKeyNameResolver;
import com.maciek.wojtaczka.encryption.framework.base.StringEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConditionalOnClass(Encrypt.class)
public class EncryptionConfiguration {


	@Bean
	public EntityEncryptor<String> encryptor(FieldEncryptor<String> stringFieldEncryptor, KeyNameResolver keyNameResolver,
											 @Value("${encryption.framework.blindId.algorithm:HmacSHA256}") String hashingAlgorithm) {
		return new EntityStringFieldsEncryptor(stringFieldEncryptor, keyNameResolver, hashingAlgorithm);
	}

	@Bean
	@ConditionalOnMissingBean(KeyNameResolver.class)
	public KeyNameResolver keyNameResolver() {
		return new StaticKeyNameResolver();
	}

	@Bean
	public FieldEncryptor<String> stringFieldEncryptor(EncryptionKeyProvider encryptionKeyProvider) {
		CipherMechanism cipherMechanism = new AesGcmNoPaddingMechanism();
		CipherMechanism blindIdHash = new HmacSha256Mechanism();
		EncryptionFacade encryptionFacade = new EncryptionFacade(Set.of(cipherMechanism, blindIdHash), encryptionKeyProvider);
		return new StringEncryptor(encryptionFacade);
	}

	@Bean
	@ConditionalOnMissingBean(EncryptionKeyProvider.class)
	public EncryptionKeyProvider encryptionKeyProvider() {
		return new InMemoryStaticKeyProvider();
	}

	@Bean
	public BlindIdConverter<String> blindIdConverter(FieldEncryptor<String> stringFieldEncryptor, KeyNameResolver keyNameResolver) {
		return new BlindIdConverter<>(stringFieldEncryptor, keyNameResolver, "HmacSHA256");
	}
}
