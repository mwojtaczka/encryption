package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.model.Person;
import com.maciek.wojtaczka.dummy.project.repository.entity.PersonEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PersonRepositoryService {

	private final PersonRepository personRepository;
	private final PersonMapper personMapper;

	public PersonRepositoryService(PersonRepository personRepository, PersonMapper personMapper) {
		this.personRepository = personRepository;
		this.personMapper = personMapper;
	}

	public List<Person> findAll() {

		return personRepository.findAll().stream()
			.map(personMapper::toModel)
			.collect(Collectors.toList());
	}

	public Person save(Person person) {
		PersonEntity personEntity = personMapper.toEntity(person);
		PersonEntity saved = personRepository.save(personEntity);

		return personMapper.toModel(saved);
	}

	public Optional<Person> FindById(long id) {

		return personRepository.findById(id)
			.map(personMapper::toModel);
	}



}
