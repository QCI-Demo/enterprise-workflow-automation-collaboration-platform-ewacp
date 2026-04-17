package com.ewacp.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ewacp.scheduler.model.ScheduledTask;
import com.ewacp.scheduler.repo.ScheduledTaskRepository;

class TaskSchedulerApiIntegrationTest extends AbstractPostgresIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ScheduledTaskRepository taskRepository;

	private String baseUrl() {
		return "http://localhost:" + port + "/api/v1/tasks";
	}

	@Test
	void createUpdateAndFetchTaskEndToEnd() {
		Map<String, Object> createBody = Map.of("name", "Daily sync", "cronExpression", "0 0 * * * ?",
				"enabled", true, "actionType", "HTTP_CALLBACK", "payload", "{\"url\":\"https://example.com/hook\"}");

		ResponseEntity<Map<String, Object>> createResp = restTemplate.exchange(baseUrl(), HttpMethod.POST,
				new HttpEntity<>(createBody), new ParameterizedTypeReference<Map<String, Object>>() {
				});
		assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResp.getBody()).isNotNull();
		Map<String, Object> created = createResp.getBody();
		Number idNum = (Number) created.get("id");
		long id = idNum.longValue();
		assertThat(created.get("version")).isNotNull();

		ScheduledTask afterCreate = taskRepository.findById(id).orElseThrow();
		assertThat(afterCreate.getName()).isEqualTo("Daily sync");
		assertThat(afterCreate.getCronExpression()).isEqualTo("0 0 * * * ?");
		assertThat(afterCreate.isEnabled()).isTrue();
		assertThat(afterCreate.getActionType()).isEqualTo("HTTP_CALLBACK");
		assertThat(afterCreate.getPayload()).contains("example.com");

		ResponseEntity<Map<String, Object>> getResp = restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.GET,
				null, new ParameterizedTypeReference<Map<String, Object>>() {
				});
		assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResp.getBody()).isNotNull();
		assertThat(getResp.getBody().get("name")).isEqualTo("Daily sync");

		long version = ((Number) getResp.getBody().get("version")).longValue();
		Map<String, Object> updateBody = Map.of("expectedVersion", version, "name", "Daily sync v2",
				"cronExpression", "0 30 * * * ?", "enabled", false, "actionType", "HTTP_CALLBACK",
				"payload", "{}");

		ResponseEntity<Map<String, Object>> putResp = restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.PUT,
				new HttpEntity<>(updateBody), new ParameterizedTypeReference<Map<String, Object>>() {
				});
		assertThat(putResp.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(putResp.getBody()).isNotNull();
		assertThat(putResp.getBody().get("name")).isEqualTo("Daily sync v2");

		ScheduledTask afterUpdate = taskRepository.findById(id).orElseThrow();
		assertThat(afterUpdate.getName()).isEqualTo("Daily sync v2");
		assertThat(afterUpdate.getCronExpression()).isEqualTo("0 30 * * * ?");
		assertThat(afterUpdate.isEnabled()).isFalse();

		ResponseEntity<List<Map<String, Object>>> listResp = restTemplate.exchange(baseUrl(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
		assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(listResp.getBody()).hasSize(1);
	}
}
