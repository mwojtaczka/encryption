package com.maciek.wojtaczka.encryption.framework.spring;

import com.maciek.wojtaczka.encryption.framework.base.EntityUpdater;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

public class JpaEntityUpdater implements EntityUpdater {

	private final EntityManager entityManager;

	public JpaEntityUpdater(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public <E> void updateEntity(E entity) {

		entityManager.merge(entity);
	}
}
