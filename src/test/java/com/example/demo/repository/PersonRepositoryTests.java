package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
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

	@BeforeEach
	public void beforeTest() {
		this.personRepository.deleteAll();
	}

	@Test
	public void insertWorks() {
		assertThat(this.personRepository.count())
			.isEqualTo(0);

		Person jpaSavedPerson = this.personRepository
			.save(new Person().name("Eric").ssn("012-34-5678"));

		Person selectedPerson = this.jdbcTemplate.queryForObject(
			"SELECT id, name, ssn FROM people",
			(resultSet, rowNum) -> new Person()
				.id(resultSet.getLong("id"))
				.name(resultSet.getString("name"))
				.ssn(resultSet.getString("ssn"))
		);

		assertThat(selectedPerson)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(jpaSavedPerson);
	}

	@Test
	public void selectWorks() {
		assertThat(this.personRepository.count())
			.isEqualTo(0);

		assertThat(this.jdbcTemplate.update("INSERT INTO people (name, ssn) VALUES ('Eric', '012-34-5678')"))
			.isEqualTo(1);

		// Since the id is generated we won't know what the starting id might be
		List<Person> people = this.personRepository.findAll();

		assertThat(people)
			.hasSize(1);

		Person person = people.get(0);

		assertThat(person.getId())
			.isNotNull()
			.isGreaterThanOrEqualTo(0);

		assertThat(person)
			.isNotNull()
			.extracting(
				Person::getName,
				Person::getSsn
			)
			.containsExactly(
				"Eric",
				"012-34-5678"
			);
	}

	@TestConfiguration
	static class PersonRepositoryTestsConfig {
		@Bean
		public JdbcTemplate jdbcTemplate(DataSource dataSource) {
			return new JdbcTemplate(dataSource);
		}
	}
}
