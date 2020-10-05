package com.maciek.wojtaczka.dummy.project.repository.entity;

import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Person")
public class PersonEntity {

	@Id
	@GeneratedValue
	Long id;
	String name;
	@Encrypt(searchable = true)
	String surname;
	String surnameBlindId;
	@Column(unique = true)
	String nickname;
	@Encrypt(searchable = true)
	String maritalStatus;
	String maritalStatusBlindId;

}
