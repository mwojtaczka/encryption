package com.maciek.wojtaczka.encryption.framework.spring.autoconfigure;

import com.maciek.wojtaczka.encryption.framework.base.EntityUpdater;
import com.maciek.wojtaczka.encryption.framework.spring.JpaEntityUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@ConditionalOnBean(EntityManager.class)
@Configuration
public class EncryptionJpaConfiguration {

	@Bean
	@ConditionalOnMissingBean(EntityUpdater.class)
	EntityUpdater entityUpdater(EntityManager entityManager) {
		return new JpaEntityUpdater(entityManager);
	}
}
