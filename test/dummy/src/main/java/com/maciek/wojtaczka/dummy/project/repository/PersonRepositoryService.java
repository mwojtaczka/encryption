package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.model.Person;
import com.maciek.wojtaczka.dummy.project.repository.entity.PersonEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
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

	public List<Person> saveAll(List<Person> people) {
		Set<PersonEntity> personEntities = people.stream()
										  .map(personMapper::toEntity)
										  .collect(Collectors.toSet());

		List<PersonEntity> savedEntities = personRepository.saveAll(personEntities);

		return savedEntities.stream()
				.map(personMapper::toModel)
				.collect(Collectors.toList());
	}

	public void deleteAll() {
		personRepository.deleteAll();
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

	public List<Person> findBySurname(String surname) {

		return personRepository.findBySurnameBlindId(surname).stream()
							   .map(personMapper::toModel)
							   .collect(Collectors.toList());
	}

	public Optional<Person> findByNickname(String nickname) {
		return personRepository.findByNickname(nickname)
							   .map(personMapper::toModel);
	}

	public List<Person> findByNameAndSurname(String name, String surname) {

		return personRepository.findByNameAndSurnameBlindId(name, surname).stream()
							   .map(personMapper::toModel)
							   .collect(Collectors.toList());
	}

	@Transactional
	public List<Person> findByNameAndSurnameAndMaritalStatus(String name, String surname, String maritalStatus) {

		return personRepository.findByNameAndSurnameBlindIdAndMaritalStatusBlindId(name, surname, maritalStatus).stream()
							   .map(personMapper::toModel)
							   .collect(Collectors.toList());
	}

	public List<Person> findByCustom(String name, String surname, String maritalStatus) {

		return personRepository.findCustom(name, surname, maritalStatus).stream()
							   .map(personMapper::toModel)
							   .collect(Collectors.toList());
	}

	public List<Person> findBySurnameInList(List<String> surnames) {

		return personRepository.findBySurnameBlindIdIn(surnames).stream()
							   .map(personMapper::toModel)
							   .collect(Collectors.toList());
	}

	public int countBySurname(String surname) {
		return personRepository.countBySurnameBlindId(surname);
	}


}
