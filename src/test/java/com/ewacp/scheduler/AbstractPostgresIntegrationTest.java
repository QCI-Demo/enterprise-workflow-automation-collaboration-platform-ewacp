package com.ewacp.scheduler;

import java.io.IOException;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

/**
 * Starts PostgreSQL either via Testcontainers (when Docker is available) or Zonky embedded
 * binaries (CI environments without Docker) so integration tests run in both setups.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractPostgresIntegrationTest {

	private static final boolean DOCKER_AVAILABLE = DockerClientFactory.instance().isDockerAvailable();

	private static PostgreSQLContainer<?> postgresContainer;
	private static EmbeddedPostgres embeddedPostgres;

	static {
		if (DOCKER_AVAILABLE) {
			postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine");
			postgresContainer.start();
		} else {
			try {
				embeddedPostgres = EmbeddedPostgres.start();
			} catch (IOException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (postgresContainer != null) {
				postgresContainer.stop();
			}
			if (embeddedPostgres != null) {
				try {
					embeddedPostgres.close();
				} catch (IOException ignored) {
					// best-effort shutdown
				}
			}
		}));
	}

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		if (postgresContainer != null) {
			registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
			registry.add("spring.datasource.username", postgresContainer::getUsername);
			registry.add("spring.datasource.password", postgresContainer::getPassword);
		} else {
			registry.add("spring.datasource.url",
					() -> embeddedPostgres.getJdbcUrl("postgres", "postgres"));
			registry.add("spring.datasource.username", () -> "postgres");
			registry.add("spring.datasource.password", () -> "postgres");
		}
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
	}
}
