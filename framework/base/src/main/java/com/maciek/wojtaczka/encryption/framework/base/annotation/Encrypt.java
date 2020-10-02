package com.maciek.wojtaczka.encryption.framework.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {

	boolean lazy() default false;

	String algorithm() default "AES/GCM/NoPadding";

	boolean searchable() default false;

	String blindIdAlgorithm() default "HmacSHA256";
}
