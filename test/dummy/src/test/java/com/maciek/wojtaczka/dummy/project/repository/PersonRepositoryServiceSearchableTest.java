package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PersonRepositoryServiceSearchableTest {

	@Autowired
	private PersonRepositoryService repositoryService;

	@AfterEach
	void cleanup() {
		repositoryService.deleteAll();
	}

	@Test
	void shouldFindTwoEntities_whenFindByEncryptedSurname() {
		//given
		Person person1 = Person.builder()
							   .name("Jenny")
							   .surname("Smith")
							   .build();
		Person person2 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .build();
		repositoryService.saveAll(List.of(person1, person2));

		//when
		List<Person> smiths = repositoryService.findBySurname("Smith");

		//then
		assertThat(smiths).hasSize(2);
	}

	@Test
	void shouldFindTwoEntities_whenFindByNonEncryptedNameAndEncryptedSurnameAndMaritalStatus() {
		//given
		Person person1 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .maritalStatus("married")
							   .nickname("A")
							   .build();
		Person person2 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .maritalStatus("married")
							   .nickname("B")
							   .build();
		repositoryService.saveAll(List.of(person1, person2));

		//when
		List<Person> smiths = repositoryService.findByNameAndSurnameAndMaritalStatus("John", "Smith", "married");

		//then
		assertThat(smiths).hasSize(2);
	}

	@Test
	@Disabled("Requires implementation") //TODO
	void shouldFindTwoEntities_whenFindByCustomQuery() {
		//given
		Person person1 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .maritalStatus("married")
							   .nickname("A")
							   .build();
		Person person2 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .maritalStatus("married")
							   .nickname("B")
							   .build();
		repositoryService.saveAll(List.of(person1, person2));

		//when
		List<Person> smiths = repositoryService.findByCustom("John", "Smith", "married");

		//then
		assertThat(smiths).hasSize(2);
	}

	@Test
	@Disabled("Requires implementation") //TODO
	void shouldFindTwoEntities_whenFindBySurnameList() {
		//given
		Person person1 = Person.builder()
							   .name("John")
							   .surname("Doe")
							   .build();
		Person person2 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .build();
		Person person3 = Person.builder()
							   .name("John")
							   .surname("Snow")
							   .build();

		repositoryService.saveAll(List.of(person1, person2, person3));

		//when
		List<Person> all = repositoryService.findBySurnameInList(List.of("Doe", "Smith"));
		Person found1 = all.get(0);
		Person found2 = all.get(1);

		//then
		assertThat(found1.getSurname()).isIn("Doe", "Smith");
		assertThat(found2.getSurname()).isIn("Doe", "Smith");
	}

	@Test
	@Disabled("Requires implementation") //TODO
	void shouldCountTwoBySurname() {
		//given
		Person person1 = Person.builder()
							   .name("Joanna")
							   .surname("Smith")
							   .build();
		Person person2 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .build();

		repositoryService.saveAll(List.of(person1, person2));

		//when
		int countBySurname = repositoryService.countBySurname("Smith");

		//then
		assertThat(countBySurname).isEqualTo(2);
	}
}
