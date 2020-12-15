package com.example.demo.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Person;
import com.example.demo.repository.PersonRepository;

@RestController
@RequestMapping("/people")
public class PersonAPI {
	private final PersonRepository personRepository;

	public PersonAPI(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Person> getAll() {
		return this.personRepository.findAll();
	}
}
