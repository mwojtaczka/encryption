package com.maciek.wojtaczka.dummy.project.repository.entity;

import com.maciek.wojtaczka.encryption.framework.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Value
@Builder
@Entity
public class PersonEntity {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@Encrypt
	private String surname;

}
