package com.maciek.wojtaczka.encryption.framework.base;


import com.maciek.wojtaczka.encryption.core.CipherRecord;
import com.maciek.wojtaczka.encryption.core.EncryptionKey;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;
import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StringStaleEncryptionPredicateTest {

	@Mock
	private EncryptionKeyProvider encryptionKeyProvider;

	@InjectMocks
	private StringStaleEncryptionPredicate predicate;

	@Test
	void shouldReturnTrue_whenOneOfTheRecordsEncryptedLowerKeyVersionThenLatest() {

		//given
		StringCipherRecordConverter converter = new StringCipherRecordConverter();
		CipherRecord freshRecord = new CipherRecord(new byte[0], "algorithm", "keyName", 2);
		CipherRecord staleRecord = new CipherRecord(new byte[0], "algorithm", "keyName", 1);
		Entity entity = Entity.builder()
							  .fresh(converter.convertToString(freshRecord))
							  .stale(converter.convertToString(staleRecord))
							  .build();
		EncryptionKey latestKey = EncryptionKey.of("keyName", null, 2);
		when(encryptionKeyProvider.getLatestKey("keyName", "algorithm")).thenReturn(latestKey);

		//when
		boolean stale = predicate.isStale(entity);

		//then
		assertThat(stale).isTrue();
	}

	@Test
	void shouldReturnFalse_whenAllRecordsEncryptedWithLatestKeyVersion() {

		//given
		StringCipherRecordConverter converter = new StringCipherRecordConverter();
		CipherRecord freshRecord = new CipherRecord(new byte[0], "algorithm", "keyName", 2);
		CipherRecord staleRecord = new CipherRecord(new byte[0], "algorithm", "keyName", 2);
		Entity entity = Entity.builder()
							  .fresh(converter.convertToString(freshRecord))
							  .stale(converter.convertToString(staleRecord))
							  .build();
		EncryptionKey latestKey = EncryptionKey.of("keyName", null, 2);
		when(encryptionKeyProvider.getLatestKey("keyName", "algorithm")).thenReturn(latestKey);

		//when
		boolean stale = predicate.isStale(entity);

		//then
		assertThat(stale).isFalse();
	}

	@Value
	@Builder
	private static class Entity {

		@Encrypt
		String fresh;

		@Encrypt
		String stale;
	}


}
