package com.maciek.wojtaczka.encryption.core;

import lombok.Value;

@Value(staticConstructor = "of")
class CipherResult {

	private byte[] cipherContent;
	private byte[] iv;
	private String cipherMechanism;

}
