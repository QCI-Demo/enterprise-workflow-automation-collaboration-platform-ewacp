package com.ewacp.scheduler.service;

import java.util.List;

import com.ewacp.scheduler.api.dto.CreateTaskRequest;
import com.ewacp.scheduler.api.dto.TaskResponse;
import com.ewacp.scheduler.api.dto.UpdateTaskRequest;

public interface TaskService {

	TaskResponse create(CreateTaskRequest request);

	TaskResponse update(Long id, UpdateTaskRequest request);

	TaskResponse get(Long id);

	List<TaskResponse> listAll();

	/**
	 * Ensures every persisted task has a valid cron expression. Used when rebuilding schedules.
	 */
	void validateAllCronExpressions();
}
