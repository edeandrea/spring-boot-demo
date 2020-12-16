package com.example.demo.api;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ReflectionUtils;

import com.example.demo.domain.Person;
import com.example.demo.repository.PersonRepository;

@WebMvcTest(PersonAPI.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class PersonAPITests {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonRepository personRepository;

	@Test
	public void getAllWorks() throws Exception {
		given(this.personRepository.findAll())
			.willReturn(List.of(
				new Person()
					.id(1L)
					.name("Eric")
					.ssn("123456987")
			));

		this.mockMvc.perform(
			request(HttpMethod.GET, "/people")
			.accept(MediaType.APPLICATION_JSON)
		)
			.andExpect(handler().handlerType(PersonAPI.class))
			.andExpect(handler().method(ReflectionUtils.findMethod(PersonAPI.class, "getAll")))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", Matchers.hasSize(1)))
			.andExpect(jsonPath("$[0]").exists())
			.andExpect(jsonPath("$[0].id").value(1L))
			.andExpect(jsonPath("$[0].name").value("Eric"))
			.andExpect(jsonPath("$[0].ssn").value("123456987"));

		verify(this.personRepository, only()).findAll();
	}

	@Test
	public void invalidPersonNoSsn() throws Exception {
		this.mockMvc.perform(
			request(HttpMethod.POST, "/people")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"name\": \"Eric\"}")
		)
			.andExpect(handler().handlerType(PersonAPI.class))
			.andExpect(handler().method(ReflectionUtils.findMethod(PersonAPI.class, "addPerson", Person.class)))
			.andExpect(status().isBadRequest());

		verifyNoInteractions(this.personRepository);
	}

	@Test
	public void findByIdInvalidId() throws Exception {
		this.mockMvc.perform(
			request(HttpMethod.GET, "/people/{id}", -1L)
				.accept(MediaType.APPLICATION_JSON)
		)
			.andExpect(handler().handlerType(PersonAPI.class))
			.andExpect(status().isBadRequest());

		verifyNoInteractions(this.personRepository);
	}

	@Test
	public void validPerson() throws Exception {
		given(this.personRepository.save(any(Person.class)))
			.willReturn(
				new Person()
					.id(1L)
					.name("Eric")
					.ssn("123456987")
			);

		this.mockMvc.perform(
			request(HttpMethod.POST, "/people")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"Eric\", \"ssn\": \"123456987\"}")
		)
			.andExpect(handler().handlerType(PersonAPI.class))
			.andExpect(handler().method(ReflectionUtils.findMethod(PersonAPI.class, "addPerson", Person.class)))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").exists())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.name").value("Eric"))
			.andExpect(jsonPath("$.ssn").value("123456987"));

		verify(this.personRepository, only()).save(any(Person.class));
	}
}
