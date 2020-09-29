package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PersonRepositoryServiceTest {

	@Autowired
	private PersonRepositoryService repositoryService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void shouldEncryptSurnameColumn_whenEntitySaved() {
		//given
		Person person = Person.builder()
							  .name("John")
							  .surname("Doe")
							  .build();

		//when
		Person saved = repositoryService.save(person);
		Object[] args = { saved.getId() };
		String surnameColumnValue = jdbcTemplate.queryForObject("SELECT surname FROM person WHERE id=?", args, String.class);

		//then
		assertThat(saved.getSurname()).isEqualTo("Doe");
		assertThat(surnameColumnValue).doesNotContain("Doe");
	}

	@Test
	void shouldEncryptSurnameColumn_whenEntityModified() {
		//given
		Person person = Person.builder()
							  .name("John")
							  .surname("Doe")
							  .build();
		repositoryService.save(person);

		//when
		Person updatedPerson = Person.builder()
									 .name(person.getName())
									 .surname("Smith")
									 .build();
		Person updatedEntity = repositoryService.save(updatedPerson);
		Object[] args = { updatedEntity.getId() };
		String surnameColumnValue = jdbcTemplate.queryForObject("SELECT surname FROM person WHERE id=?", args, String.class);

		//then
		assertThat(updatedEntity.getSurname()).isEqualTo("Smith");
		assertThat(surnameColumnValue).doesNotContain("Doe");
	}

	@Test
	void shouldEncryptSurnameColumn_whenSetOfEntitiesSaved() {
		//given
		Person person1 = Person.builder()
							   .name("John")
							   .surname("Doe")
							   .build();
		Person person2 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .build();

		//when
		List<Person> saved = repositoryService.saveAll(Set.of(person1, person2));
		Person saved1 = saved.get(0);
		Person saved2 = saved.get(1);
		Object[] args1 = { saved1.getId() };
		Object[] args2 = { saved2.getId() };
		String surnameColumnValue1 = jdbcTemplate.queryForObject("SELECT surname FROM person WHERE id=?", args1, String.class);
		String surnameColumnValue2 = jdbcTemplate.queryForObject("SELECT surname FROM person WHERE id=?", args2, String.class);

		//then
		assertThat(saved1.getSurname()).isIn("Doe", "Smith");
		assertThat(saved2.getSurname()).isIn("Doe", "Smith");
		assertThat(surnameColumnValue1).doesNotContain("Doe", "Smith");
		assertThat(surnameColumnValue2).doesNotContain("Doe", "Smith");
	}

	@Test
	void shouldDecryptSurname_whenFindById() {
		//given
		Person person = Person.builder()
							  .name("John")
							  .surname("Doe")
							  .build();
		Person saved = repositoryService.save(person);

		//when
		Person found = repositoryService.findById(saved.getId())
										  .get();

		//then
		assertThat(found.getSurname()).isEqualTo("Doe");
	}

	//TODO: create case with findOne()

	@Test
	void shouldDecryptSurname_whenFindAll() {
		//given
		Person person1 = Person.builder()
							   .name("John")
							   .surname("Doe")
							   .build();
		Person person2 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .build();
		repositoryService.saveAll(Set.of(person1, person2));

		//when
		List<Person> all = repositoryService.findAll();
		Person found1 = all.get(0);
		Person found2 = all.get(1);

		//then
		assertThat(found1.getSurname()).isIn("Doe", "Smith");
		assertThat(found2.getSurname()).isIn("Doe", "Smith");
	}

	@Test
	void shouldDecryptSurname_whenFindByName() {
		//given
		Person person1 = Person.builder()
							   .name("John")
							   .surname("Doe")
							   .build();
		Person person2 = Person.builder()
							   .name("John")
							   .surname("Smith")
							   .build();
		repositoryService.saveAll(Set.of(person1, person2));

		//when
		List<Person> all = repositoryService.findByName("John");
		Person found1 = all.get(0);
		Person found2 = all.get(1);

		//then
		assertThat(found1.getSurname()).isIn("Doe", "Smith");
		assertThat(found2.getSurname()).isIn("Doe", "Smith");
	}

	@Test
	void shouldDecryptSurname_whenFindByUniqueNickname() {
		//given
		Person person = Person.builder()
							  .name("John")
							  .surname("Doe")
							  .nickname("Fat")
							  .build();
		repositoryService.save(person);

		//when
		Person found = repositoryService.findByNickname("Fat")
										.get();

		//then
		assertThat(found.getSurname()).isEqualTo("Doe");
	}

}
