package com.ewacp.scheduler.api;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ewacp.scheduler.api.dto.CreateTaskRequest;
import com.ewacp.scheduler.api.dto.TaskResponse;
import com.ewacp.scheduler.api.dto.UpdateTaskRequest;
import com.ewacp.scheduler.service.TaskService;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
		return taskService.create(request);
	}

	@PutMapping("/{id}")
	public TaskResponse update(@PathVariable("id") Long id, @Valid @RequestBody UpdateTaskRequest request) {
		return taskService.update(id, request);
	}

	@GetMapping("/{id}")
	public TaskResponse get(@PathVariable("id") Long id) {
		return taskService.get(id);
	}

	@GetMapping
	public List<TaskResponse> list() {
		return taskService.listAll();
	}
}
