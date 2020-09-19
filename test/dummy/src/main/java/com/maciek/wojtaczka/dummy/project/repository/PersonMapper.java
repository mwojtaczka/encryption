package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.model.Person;
import com.maciek.wojtaczka.dummy.project.repository.entity.PersonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {

	PersonEntity toEntity(Person person);

	Person toModel(PersonEntity personEntity);
}
