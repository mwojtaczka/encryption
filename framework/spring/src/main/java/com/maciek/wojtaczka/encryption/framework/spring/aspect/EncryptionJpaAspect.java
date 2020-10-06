package com.maciek.wojtaczka.encryption.framework.spring.aspect;

import com.maciek.wojtaczka.encryption.framework.base.EntityEncryptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.List;
import java.util.Optional;

@Aspect
public class EncryptionJpaAspect <C> {

	private final EntityEncryptor<C> encryptor;

	public EncryptionJpaAspect(EntityEncryptor<C> encryptor) {
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

	@Pointcut("execution(* *..findById(*))")
	public void findById() {}

	@Pointcut("execution(* *..findOne(*))")
	public void findOne() {}

	@Around("jpaRepository() && (findById() || findOne())")
	public <S> Optional<S> findOneProxy(ProceedingJoinPoint joinPoint) throws Throwable {

		Optional<S> optionalEntity = (Optional<S>) joinPoint.proceed();
		optionalEntity.ifPresent(encryptor::decryptObject);

		return optionalEntity;
	}

	@Pointcut("execution(* *..findAll())")
	public void findAll() {}

	@Around("jpaRepository() && findAll()")
	public <S> List<S> findAllProxy(ProceedingJoinPoint joinPoint) throws Throwable {

		List<S> list = (List<S>) joinPoint.proceed();
		list.forEach(encryptor::decryptObject);

		return list;
	}

	@Pointcut("execution(* *..findBy*(*))")
	public void findByGeneric() {}

	@Around("jpaRepository() && findByGeneric() && !(findAll() || findById() || findOne())")
	public <T, S> T findByGenericProxy(ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = joinPoint.proceed();
		if (result instanceof Optional) {
			Optional<S> optionalEntity = (Optional<S>) result;
			optionalEntity.ifPresent(encryptor::decryptObject);

			return (T) optionalEntity;
		} else {
			Iterable<S> iterable = (Iterable<S>) result;
			iterable.forEach(encryptor::decryptObject);

			return (T) iterable;
		}
	}
}
