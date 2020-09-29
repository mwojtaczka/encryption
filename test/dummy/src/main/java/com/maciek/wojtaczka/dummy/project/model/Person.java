package com.maciek.wojtaczka.dummy.project.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Person {

	Long id;
	String name;
	String surname;
	String nickname;

}
