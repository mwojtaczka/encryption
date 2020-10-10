package com.maciek.wojtaczka.encryption.framework.base;

import java.util.concurrent.CompletableFuture;

public class AsyncReencryptDecorator<F> implements EntityEncryptor<F> {

	private final EntityEncryptor<F> entityEncryptor;
	private final StaleEncryptionPredicate predicate;
	private final EntityUpdater entityUpdater;


	public AsyncReencryptDecorator(EntityEncryptor<F> entityEncryptor, StaleEncryptionPredicate predicate,
								   EntityUpdater entityUpdater) {
		this.entityEncryptor = entityEncryptor;
		this.predicate = predicate;
		this.entityUpdater = entityUpdater;
	}

	@Override
	public void encryptObject(Object entity) {
		entityEncryptor.encryptObject(entity);
	}

	@Override
	public void encryptObject(Object entity, String keyName) {
		entityEncryptor.encryptObject(entity, keyName);
	}

	@Override
	public void decryptObject(Object entity) {
		boolean isStale = predicate.isStale(entity);
		entityEncryptor.decryptObject(entity);
		CompletableFuture.runAsync(() -> {
		if (isStale) {
				encryptObject(entity);
				entityUpdater.updateEntity(entity);
			}
		});
	}

	@Override
	public void decryptObject(Object entity, String keyName) {
		boolean isStale = predicate.isStale(entity);
		entityEncryptor.decryptObject(entity, keyName);
		CompletableFuture.runAsync(() -> {
			if (isStale) {
				encryptObject(entity);
				entityUpdater.updateEntity(entity);
			}
		});
	}
}
