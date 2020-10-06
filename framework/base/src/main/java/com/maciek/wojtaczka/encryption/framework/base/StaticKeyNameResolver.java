package com.maciek.wojtaczka.encryption.framework.base;

public class StaticKeyNameResolver implements KeyNameResolver {

		@Override
		public String resolveEncryptionKeyName(Object entity) {
			return "encryption-key";
		}

		@Override
		public String resolveBlindIdKeyName() {
			return "blind-id-key";
		}
	}
