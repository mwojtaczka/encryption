package com.maciek.wojtaczka.encryption.test.benchmark.framework;

import com.maciek.wojtaczka.encryption.core.AesGcmNoPaddingMechanism;
import com.maciek.wojtaczka.encryption.core.CipherMechanism;
import com.maciek.wojtaczka.encryption.core.EncryptionFacade;
import com.maciek.wojtaczka.encryption.core.EncryptionKeyProvider;
import com.maciek.wojtaczka.encryption.core.HmacSha256Mechanism;
import com.maciek.wojtaczka.encryption.framework.base.EntityEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.FieldEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.GenericEntityEncryptor;
import com.maciek.wojtaczka.encryption.framework.base.InMemoryStaticKeyProvider;
import com.maciek.wojtaczka.encryption.framework.base.KeyNameResolver;
import com.maciek.wojtaczka.encryption.framework.base.StaticKeyNameResolver;
import com.maciek.wojtaczka.encryption.framework.base.StringEncryptor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.List;
import java.util.Set;

public class EntityEncryptorBenchmark {

	@State(Scope.Benchmark)
	public static class AppState {

		public EntityEncryptor<String> entityEncryptor;
		public DummyEntity dummyEntityOneField;
		public DummyEntity dummyEntityOneFieldEncrypted;
		public DummyEntity dummyEntityFourFields;
		public DummyEntity dummyEntityFourFieldsEncrypted;
		public DummyEntity dummyEntityOneElementList;
		public DummyEntity dummyEntityTenElementList;
		public DummyEntity entityFourFieldsAndEmbeddedEntityFourFields;

		@Setup
		public void setup() {
			setupEntityEncryptor();

			dummyEntityOneField = DummyEntity.builder()
											 .sensitive1("sensitive1")
											 .build();
			dummyEntityOneFieldEncrypted = dummyEntityOneField.getCopy();
			entityEncryptor.encryptObject(dummyEntityOneFieldEncrypted);

			dummyEntityFourFields = DummyEntity.builder()
											   .sensitive1("sensitive1")
											   .sensitive2("sensitive2")
											   .sensitive3("sensitive3")
											   .sensitive4("sensitive4")
											   .build();
			dummyEntityFourFieldsEncrypted = dummyEntityFourFields.getCopy();
			entityEncryptor.encryptObject(dummyEntityFourFieldsEncrypted);

			dummyEntityOneElementList = DummyEntity.builder()
												   .sensitiveList(List.of("sensitive1"))
												   .build();

			dummyEntityTenElementList = DummyEntity.builder()
												   .sensitiveList(List.of("sensitive1", "sensitive2", "sensitive3", "sensitive4", "sensitive5",
																		  "sensitive6", "sensitive7", "sensitive8", "sensitive9", "sensitive10"))
												   .build();

			entityFourFieldsAndEmbeddedEntityFourFields = DummyEntity.builder()
																	 .sensitive1("sensitive1")
																	 .sensitive2("sensitive2")
																	 .sensitive3("sensitive3")
																	 .sensitive4("sensitive4")
																	 .embeddedEntity(dummyEntityFourFields.getCopy())
																	 .build();
		}

		public void setupEntityEncryptor() {
			CipherMechanism aesGcmNoPaddingMechanism = new AesGcmNoPaddingMechanism();
			CipherMechanism hmacShaMechanism = new HmacSha256Mechanism();
			EncryptionKeyProvider keyProvider = new InMemoryStaticKeyProvider();
			EncryptionFacade encryptionFacade = new EncryptionFacade(Set.of(aesGcmNoPaddingMechanism, hmacShaMechanism), keyProvider);
			FieldEncryptor<String> stringEncryptor = new StringEncryptor(encryptionFacade);
			KeyNameResolver keyNameResolver = new StaticKeyNameResolver();
			entityEncryptor = new GenericEntityEncryptor<>(stringEncryptor, keyNameResolver, "HmacSHA256", String.class);
		}
	}

	@Benchmark
	@Fork(value = 1, warmups = 2)
	public void aEncryptEntityOneField(AppState state) {
		state.entityEncryptor.encryptObject(state.dummyEntityOneField.getCopy());
	}

	@Benchmark
	@Fork(value = 1, warmups = 2)
	public void bEncryptEntityFourFields(AppState state) {
		state.entityEncryptor.encryptObject(state.dummyEntityFourFields.getCopy());
	}

	@Benchmark
	@Fork(value = 1, warmups = 2)
	public void cEncryptEntityOneElementList(AppState state) {
		state.entityEncryptor.encryptObject(state.dummyEntityOneElementList.getCopy());
	}

	@Benchmark
	@Fork(value = 1, warmups = 2)
	public void dEncryptEntityTenElementList(AppState state) {
		state.entityEncryptor.encryptObject(state.dummyEntityTenElementList.getCopy());
	}

	@Benchmark
	@Fork(value = 1, warmups = 2)
	public void eEncryptEntityFourFieldsAndEmbeddedFourFields(AppState state) {
		state.entityEncryptor.encryptObject(state.dummyEntityTenElementList.getCopy());
	}

	@Benchmark
	@Fork(value = 1, warmups = 2)
	public void fDecryptEntityOneField(AppState state) {
		state.entityEncryptor.decryptObject(state.dummyEntityOneFieldEncrypted.getCopy());
	}

	@Benchmark
	@Fork(value = 1, warmups = 2)
	public void gDecryptEntityFourFields(AppState state) {
		state.entityEncryptor.decryptObject(state.dummyEntityFourFieldsEncrypted.getCopy());
	}
}
