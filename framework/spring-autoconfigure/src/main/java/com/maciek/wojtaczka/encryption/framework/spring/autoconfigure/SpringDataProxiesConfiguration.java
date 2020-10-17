package com.maciek.wojtaczka.encryption.framework.spring.autoconfigure;

import com.maciek.wojtaczka.encryption.framework.base.BlindIdConverter;
import com.maciek.wojtaczka.encryption.framework.base.EntityEncryptor;
import com.maciek.wojtaczka.encryption.framework.spring.aspect.BlindIdSearchAspect;
import com.maciek.wojtaczka.encryption.framework.spring.aspect.EncryptionJpaAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.JpaRepository;

@Configuration
@ConditionalOnClass(JpaRepository.class)
@EnableAspectJAutoProxy
public class SpringDataProxiesConfiguration {

	@Bean
	@ConditionalOnProperty(name = "encryption.framework.spring-data.proxy.encrypt.enabled", havingValue = "true", matchIfMissing = true)
	public EncryptionJpaAspect<String> encryptionJpaAspect(EntityEncryptor<String> encryptor) {
		return new EncryptionJpaAspect<>(encryptor);
	}


	@Bean
	@ConditionalOnProperty(name = "encryption.framework.spring-data.proxy.hash.enabled", havingValue = "true", matchIfMissing = true)
	public BlindIdSearchAspect<String> stringBlindIdSearchAspect(BlindIdConverter<String> blindIdConverter) {
		return new BlindIdSearchAspect<>(blindIdConverter);
	}
}
