package com.maciek.wojtaczka.dummy.project.repository.entity;

import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Value
@Builder
@Entity
@Table(name = "Person")
public class PersonEntity {

	@Id
	@GeneratedValue
	Long id;
	String name;
	@Encrypt
	String surname;

}
