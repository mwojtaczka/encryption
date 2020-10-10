package com.maciek.wojtaczka.encryption.framework.base;

import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AsyncReencryptDecoratorTest {

	@Mock
	private EntityEncryptor<?> docoratee;

	@Mock
	private StaleEncryptionPredicate predicate;

	@Mock
	private EntityUpdater entityUpdater;

	@InjectMocks
	private AsyncReencryptDecorator<?> asyncReencryptDecorator;

	@Test
	void shouldCallUpdater_whenEntityStale() {
		//given
		Entity entity = Entity.builder()
							  .sensitive("sensitive")
							  .build();
		when(predicate.isStale(any())).thenReturn(true);

		//when
		asyncReencryptDecorator.decryptObject(entity);

		//then
		verify(entityUpdater, timeout(100).times(1)).updateEntity(any());
	}

	@Test
	void shouldNotCallUpdater_whenEntityFresh() {
		//given
		Entity entity = Entity.builder()
							  .sensitive("sensitive")
							  .build();
		when(predicate.isStale(any())).thenReturn(false);

		//when
		asyncReencryptDecorator.decryptObject(entity);

		//then
		sleep50ms();
		verify(entityUpdater, never()).updateEntity(any());
	}

	private void sleep50ms() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Value
	@Builder
	private static class Entity {

		@Encrypt
		String sensitive;
	}

}
