package com.maciek.wojtaczka.encryption.framework.base;


public interface StaleEncryptionPredicate {

	boolean isStale(Object entity);
}
