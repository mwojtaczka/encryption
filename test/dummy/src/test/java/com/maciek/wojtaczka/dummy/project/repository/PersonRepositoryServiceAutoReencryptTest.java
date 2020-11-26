package com.maciek.wojtaczka.dummy.project.repository;

import com.maciek.wojtaczka.dummy.project.model.Person;
import com.maciek.wojtaczka.encryption.core.EncryptionKey;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class PersonRepositoryServiceAutoReencryptTest {

	@MockBean
	private EncryptionKeyProvider encryptionKeyProvider;

	@Autowired
	private PersonRepositoryService repositoryService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@AfterEach
	void cleanup() {
		repositoryService.deleteAll();
	}

	@Test
	void shouldAutomaticallyReencryptEntity() {
		//given
		keyProviderWIllReturnKeys();
		Person person = Person.builder()
							   .name("Jenny")
							   .surname("Smith")
							   .build();
		Person saved = repositoryService.save(person);

		keyProviderWillReturnNewKeys();

		//when
		repositoryService.findById(saved.getId());
		sleep();

		//then
		Object[] args = { saved.getId() };
		String surnameColumnValue = jdbcTemplate.queryForObject("SELECT surname FROM person WHERE id=?", args, String.class);
		assertThat(surnameColumnValue).isNotBlank();
		String keyVersion = surnameColumnValue.split(":")[3].substring(0, 1);
		assertThat(keyVersion).isEqualTo("2");
	}

	private void keyProviderWillReturnNewKeys() {
		EncryptionKey testKey2 = EncryptionKey.of("encryption-key", generateAesSecretKey(), 2);
		when(encryptionKeyProvider.getLatestKey("encryption-key", "AES/GCM/NoPadding"))
			   .thenReturn(testKey2);
	}

	private void keyProviderWIllReturnKeys() {
		EncryptionKey testKey1 = EncryptionKey.of("encryption-key", generateAesSecretKey(), 1);
		EncryptionKey testBlindIdKey = EncryptionKey.of("blind-id-key", generateAesSecretKey(), 1);
		when(encryptionKeyProvider.getLatestKey("encryption-key", "AES/GCM/NoPadding"))
			   .thenReturn(testKey1);
		when(encryptionKeyProvider.getKey("encryption-key", 1, "AES/GCM/NoPadding"))
			   .thenReturn(testKey1);
		when(encryptionKeyProvider.getLatestKey("blind-id-key", "HmacSHA256"))
			   .thenReturn(testBlindIdKey);
	}

	private void sleep() {
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private SecretKey generateAesSecretKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, "AES");
	}
}
