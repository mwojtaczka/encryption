package com.maciek.wojtaczka.encryption.framework.base;

@FunctionalInterface
public interface KeyNameResolver {

	String resolveKeyName(Object entity);
}
