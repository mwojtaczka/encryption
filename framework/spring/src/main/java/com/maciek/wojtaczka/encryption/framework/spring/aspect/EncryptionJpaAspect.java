package com.maciek.wojtaczka.encryption.framework.spring.aspect;

import com.maciek.wojtaczka.encryption.framework.base.EntityStringFieldsEncryptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.List;

@Aspect
public class EncryptionJpaAspect {

	private final EntityStringFieldsEncryptor encryptor;

	public EncryptionJpaAspect(EntityStringFieldsEncryptor encryptor) {
		this.encryptor = encryptor;
	}

	@Pointcut("target(org.springframework.data.jpa.repository.JpaRepository)")
	public void jpaRepository() {}

	@Pointcut("execution(* *..save*(*))")
	public void save() {}

	@Pointcut("execution(* *..saveAll(*))")
	public void saveAll() {}

	@Around("jpaRepository() && save() && !saveAll()")
	public <S> S saveProxy(ProceedingJoinPoint joinPoint) throws Throwable {

		Object entity = joinPoint.getArgs()[0];
		encryptor.encryptObject(entity);

		Object savedEntity = joinPoint.proceed();

		encryptor.decryptObject(savedEntity);
		return (S) savedEntity;
	}

	@Around("jpaRepository() && saveAll()")
	public <S> List<S> saveAllProxy(ProceedingJoinPoint joinPoint) throws Throwable {

		Iterable entities = (Iterable) joinPoint.getArgs()[0];
		for (Object entity: entities) {
			encryptor.encryptObject(entity);
		}

		List<S> savedEntities = (List<S>) joinPoint.proceed();

		for (S savedEntity: savedEntities) {
			encryptor.decryptObject(savedEntity);
		}

		return savedEntities;
	}
}
