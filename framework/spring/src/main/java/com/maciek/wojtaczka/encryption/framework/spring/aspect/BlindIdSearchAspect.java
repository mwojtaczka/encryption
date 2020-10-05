package com.maciek.wojtaczka.encryption.framework.spring.aspect;

import com.maciek.wojtaczka.encryption.framework.base.BlindIdConverter;
import jdk.jfr.Experimental;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Experimental
@Aspect
public class BlindIdSearchAspect<C> {

	private final BlindIdConverter<C> blindIdConverter;

	public BlindIdSearchAspect(BlindIdConverter<C> blindIdConverter) {
		this.blindIdConverter = blindIdConverter;
	}

	@Pointcut("target(org.springframework.data.jpa.repository.JpaRepository)")
	public void jpaRepository() {
	}

	@Pointcut("execution(* *..find*(*, ..))")
	public void find() {
	}

	/**
	 * This is proxy that takes all passed arguments and hashes blind indices.
	 * Current implementation requires that blind indices to be listed after regular arguments in the method signature.
	 * Example:
	 * findByNameAndAgeAndSurnameBlindIdAndPersonalNumberBlindId(String name, int age, String surname, String personalNumber)
	 * in above, surname and personalNumber will be hashed.
	 *
	 * @param joinPoint joinPoint
	 * @return result of the proxied method with new (hashed) arguments
	 * @throws Throwable
	 */
	@Around("jpaRepository() && find()")
	public Object findProxy(ProceedingJoinPoint joinPoint) throws Throwable {

		Object[] args = joinPoint.getArgs();
		Object[] newArgs = new Object[args.length];

		int blindIdCount = countBlindIds(joinPoint);
		if (args.length - blindIdCount >= 0) System.arraycopy(args, 0, newArgs, 0, args.length - blindIdCount);
		for (int i = args.length - blindIdCount; i < args.length; i++) {
			newArgs[i] = blindIdConverter.hash((C) args[i]);
		}

		return joinPoint.proceed(newArgs);
	}

	private int countBlindIds(ProceedingJoinPoint jp) {
		return jp.getSignature()
				 .toShortString()
				 .split("BlindId").length - 1;
	}
}
