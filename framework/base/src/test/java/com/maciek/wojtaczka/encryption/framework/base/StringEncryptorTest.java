package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.core.CipherRecord;
import com.maciek.wojtaczka.encryption.core.EncryptionFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_16;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StringEncryptorTest {

	@Mock
	private EncryptionFacade encryptionFacade;

	@InjectMocks
	private StringEncryptor stringEncryptor;

	@Test
	void shouldContainMetadata() {
		String toBeEncrypted = "foo_boo";
		when(encryptionFacade.encryptBytes(any(), any(), any())).thenReturn(
			new CipherRecord("encryptedFooBoo".getBytes(ISO_8859_1), "iv".getBytes(ISO_8859_1), "AES/GCM/NoPadding", "test_key", 999));

		String encrypted = stringEncryptor.encrypt(toBeEncrypted, "test_key", "AES/GCM/NoPadding");

		Assertions.assertAll(
			() -> assertThat(encrypted).contains("3:11:28:30:"), //bookmarks
			() -> assertThat(encrypted).contains("test_key"),
			() -> assertThat(encrypted).contains("AES/GCM/NoPadding"),
			() -> assertThat(encrypted).contains("999")
		);
	}

	@Test
	void shouldSendRequestToEncryptionFacadeAndDeserializeReturnedDecryptedBytes() {
		String toBeDecrypted = "3:11:28:30:999test_keyAES/GCM/NoPaddingIvEncryptedFooBoo";
		CipherRecord cr = new CipherRecord("EncryptedFooBoo".getBytes(ISO_8859_1), "Iv".getBytes(ISO_8859_1), "AES/GCM/NoPadding", "test_key", 999);
		when(encryptionFacade.decryptRecord(cr, "test_key", "AES/GCM/NoPadding")).thenReturn(
			"foo_boo".getBytes(UTF_16));

		String decrypted = stringEncryptor.decrypt(toBeDecrypted, "test_key", "AES/GCM/NoPadding");

		assertThat(decrypted).isEqualTo("foo_boo");
		verify(encryptionFacade).decryptRecord(cr, "test_key", "AES/GCM/NoPadding");
	}

}
