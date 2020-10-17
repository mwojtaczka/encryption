package com.maciek.wojtaczka.encryption.framework.spring.autoconfigure;

import com.maciek.wojtaczka.encryption.framework.base.EntityUpdater;
import com.maciek.wojtaczka.encryption.framework.spring.JpaUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@ConditionalOnBean(EntityManager.class)
@Configuration
public class EncryptionJpaConfiguration {

	@Bean
	EntityUpdater entityUpdater(EntityManager entityManager) {
		return new JpaUpdater(entityManager);
	}
}
