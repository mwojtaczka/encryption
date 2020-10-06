package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.repository.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

	List<PersonEntity> findByName(String name);

	List<PersonEntity> findBySurnameBlindId(String surname);

	/*
	 * This method will be proxied and passed arguments (that correspond to blind indices) will be automatically
	 * hashed.
	 * Nevertheless the order of the arguments is crucial, arguments that correspond to blind indices should be
	 * listed after regular ones.
	 * Example here:
	 * name arg (regular one) should be before surname (corresponds to blind id)
	 * */
	List<PersonEntity> findByNameAndSurnameBlindId(String name, String surname);

	/*
	 * This method will be proxied and passed arguments (that correspond to blind indices) will be automatically
	 * hashed.
	 * Nevertheless the order of the arguments is crucial, arguments that correspond to blind indices should be
	 * listed after regular ones.
	 * Example here:
	 * name arg (regular one) should be before surname and materialStatus (correspond to blind id)
	 * */
	//TODO: to be documented in readme
	List<PersonEntity> findByNameAndSurnameBlindIdAndMaritalStatusBlindId(String name, String surname, String maritalStatus);

	Optional<PersonEntity> findByNickname(String nickname);

	@Query("SELECT p FROM PersonEntity p WHERE p.name = :name AND p.surname = :surname AND p.maritalStatus = :maritalStatus")
	List<PersonEntity> findCustom(@Param("name") String name, @Param("surname") String surname, @Param("maritalStatus") String maritalStatus);

	int countBySurnameBlindId(String surname);

	List<PersonEntity> findBySurnameBlindIdIn(List<String> surnames);

}
