package com.ewacp.scheduler.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {

	@NotBlank
	@Size(max = 255)
	private String name;

	@NotBlank
	@Size(max = 120)
	private String cronExpression;

	@NotNull
	private Boolean enabled;

	@NotBlank
	@Size(max = 64)
	private String actionType;

	private String payload;
}
