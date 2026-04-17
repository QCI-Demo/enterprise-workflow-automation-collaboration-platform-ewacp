package com.ewacp.scheduler.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheduled_tasks")
@Getter
@Setter
public class ScheduledTask {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(name = "cron_expression", nullable = false, length = 120)
	private String cronExpression;

	@Column(nullable = false)
	private boolean enabled = true;

	@Column(name = "action_type", nullable = false, length = 64)
	private String actionType;

	@Column(name = "payload", columnDefinition = "TEXT")
	private String payload;

	@Column(name = "last_executed_at")
	private Instant lastExecutedAt;

	@Column(name = "execution_count", nullable = false)
	private long executionCount;

	@Version
	private long version;
}
