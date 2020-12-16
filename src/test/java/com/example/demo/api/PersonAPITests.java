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
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", Matchers.hasSize(1)));

		verifyNoMoreInteractions(this.personRepository);
	}
}
