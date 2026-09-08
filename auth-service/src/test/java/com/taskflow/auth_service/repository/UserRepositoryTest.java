package com.taskflow.auth_service.repository;

import com.taskflow.auth_service.entity.User;
import com.taskflow.auth_service.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
            .withDatabaseName("taskflow_test")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_returnsUser_whenExists() {
        User user = User.builder()
                .email("jpa-test@test.com")
                .password("encoded-password")
                .fullName("JPA Test")
                .role(Role.ADMIN)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("jpa-test@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("JPA Test");
    }

    @Test
    void findByEmail_returnsEmpty_whenNotExists() {
        Optional<User> found = userRepository.findByEmail("nobody@test.com");
        assertThat(found).isEmpty();
    }
}
