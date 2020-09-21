package com.maciek.wojtaczka.encryption.framework;

@FunctionalInterface
public interface KeyNameResolver {

	String resolveKeyName(Object entity);
}
