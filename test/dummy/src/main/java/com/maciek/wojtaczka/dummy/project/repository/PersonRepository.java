package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.repository.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository <PersonEntity, Long> {


}
