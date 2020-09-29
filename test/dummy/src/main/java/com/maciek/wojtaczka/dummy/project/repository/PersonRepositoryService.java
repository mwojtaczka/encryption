package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.model.Person;
import com.maciek.wojtaczka.dummy.project.repository.entity.PersonEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PersonRepositoryService {

	private final PersonRepository personRepository;
	private final PersonMapper personMapper;

	public PersonRepositoryService(PersonRepository personRepository, PersonMapper personMapper) {
		this.personRepository = personRepository;
		this.personMapper = personMapper;
	}

	public Person save(Person person) {
		PersonEntity personEntity = personMapper.toEntity(person);
		PersonEntity saved = personRepository.save(personEntity);

		return personMapper.toModel(saved);
	}

	public List<Person> saveAll(Set<Person> people) {
		Set<PersonEntity> personEntities = people.stream()
										  .map(personMapper::toEntity)
										  .collect(Collectors.toSet());

		List<PersonEntity> savedEntities = personRepository.saveAll(personEntities);

		return savedEntities.stream()
				.map(personMapper::toModel)
				.collect(Collectors.toList());
	}

	public Optional<Person> findById(long id) {

		return personRepository.findById(id)
			.map(personMapper::toModel);
	}

	public List<Person> findAll() {

		return personRepository.findAll().stream()
							   .map(personMapper::toModel)
							   .collect(Collectors.toList());
	}

	public List<Person> findByName(String name) {

		return personRepository.findByName(name).stream()
							   .map(personMapper::toModel)
							   .collect(Collectors.toList());
	}

	public Optional<Person> findByNickname(String nickname) {
		return personRepository.findByNickname(nickname)
							   .map(personMapper::toModel);
	}



}
