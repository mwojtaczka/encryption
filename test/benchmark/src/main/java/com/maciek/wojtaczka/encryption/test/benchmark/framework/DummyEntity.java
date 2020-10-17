package com.maciek.wojtaczka.encryption.test.benchmark.framework;

import com.maciek.wojtaczka.encryption.framework.base.annotation.Encrypt;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class DummyEntity {

	@Encrypt
	String sensitive1;
	@Encrypt
	String sensitive2;
	@Encrypt
	String sensitive3;
	@Encrypt
	String sensitive4;
	@Encrypt
	List<String> sensitiveList;
	@Encrypt
	DummyEntity embeddedEntity;

	DummyEntity getCopy() {
		return DummyEntity.builder()
						  .sensitive1(sensitive1)
						  .sensitive2(sensitive2)
						  .sensitive3(sensitive3)
						  .sensitive4(sensitive4)
						  .sensitiveList(sensitiveList)
						  .embeddedEntity(embeddedEntity.getCopy())
						  .build();
	}

}
