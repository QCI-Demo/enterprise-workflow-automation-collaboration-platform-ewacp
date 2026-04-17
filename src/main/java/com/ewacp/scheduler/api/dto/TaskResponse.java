package com.ewacp.scheduler.api.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskResponse {

	Long id;
	String name;
	String cronExpression;
	boolean enabled;
	String actionType;
	String payload;
	Instant lastExecutedAt;
	long executionCount;
	long version;
}
