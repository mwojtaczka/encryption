package com.maciek.wojtaczka.encryption.core;

import lombok.Value;

@Value(staticConstructor = "of")
class CipherResult {

	byte[] cipherContent;
	String cipherMechanism;

}
