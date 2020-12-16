package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.domain.Person;

@DataJpaTest
class PersonRepositoryTests {
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void insertWorks() {
		Person savedPerson = this.personRepository
			.save(new Person().name("Eric").ssn("012-34-5678"));

		Person p = this.jdbcTemplate.queryForObject(
			"SELECT * FROM people",
			(resultSet, rowNum) -> new Person()
				.id(resultSet.getLong("id"))
				.name(resultSet.getString("name"))
				.ssn(resultSet.getString("ssn"))
		);

		assertThat(p)
			.isNotNull()
			.isEqualToComparingFieldByFieldRecursively(savedPerson);
	}

	@TestConfiguration
	static class PersonRepositoryTestsConfig {
		@Bean
		public JdbcTemplate jdbcTemplate(DataSource dataSource) {
			return new JdbcTemplate(dataSource);
		}
	}
}
