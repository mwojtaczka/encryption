package com.maciek.wojtaczka.dummy.project.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Person {

	private Long id;
	private String name;
	private String surname;

}
