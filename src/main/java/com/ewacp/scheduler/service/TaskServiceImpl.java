package com.ewacp.scheduler.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ewacp.scheduler.api.dto.CreateTaskRequest;
import com.ewacp.scheduler.api.dto.TaskResponse;
import com.ewacp.scheduler.api.dto.UpdateTaskRequest;
import com.ewacp.scheduler.event.ScheduledTasksChangedEvent;
import com.ewacp.scheduler.model.ScheduledTask;
import com.ewacp.scheduler.repo.ScheduledTaskRepository;
import com.ewacp.scheduler.support.CronExpressionValidator;

@Service
public class TaskServiceImpl implements TaskService {

	private final ScheduledTaskRepository taskRepository;
	private final CronExpressionValidator cronExpressionValidator;
	private final ApplicationEventPublisher eventPublisher;

	public TaskServiceImpl(ScheduledTaskRepository taskRepository,
			CronExpressionValidator cronExpressionValidator,
			ApplicationEventPublisher eventPublisher) {
		this.taskRepository = taskRepository;
		this.cronExpressionValidator = cronExpressionValidator;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public TaskResponse create(CreateTaskRequest request) {
		cronExpressionValidator.validate(request.getCronExpression());
		ScheduledTask entity = new ScheduledTask();
		entity.setName(request.getName());
		entity.setCronExpression(request.getCronExpression());
		entity.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
		entity.setActionType(request.getActionType());
		entity.setPayload(request.getPayload());
		entity.setExecutionCount(0L);
		ScheduledTask saved = taskRepository.save(entity);
		eventPublisher.publishEvent(new ScheduledTasksChangedEvent(this));
		return toResponse(saved);
	}

	@Override
	@Transactional
	public TaskResponse update(Long id, UpdateTaskRequest request) {
		cronExpressionValidator.validate(request.getCronExpression());
		ScheduledTask entity = taskRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
		if (entity.getVersion() != request.getExpectedVersion()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Concurrent modification");
		}
		entity.setName(request.getName());
		entity.setCronExpression(request.getCronExpression());
		entity.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
		entity.setActionType(request.getActionType());
		entity.setPayload(request.getPayload());
		ScheduledTask saved = taskRepository.save(entity);
		eventPublisher.publishEvent(new ScheduledTasksChangedEvent(this));
		return toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public TaskResponse get(Long id) {
		ScheduledTask entity = taskRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
		return toResponse(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskResponse> listAll() {
		return taskRepository.findAllByOrderByIdAsc().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public void validateAllCronExpressions() {
		for (ScheduledTask task : taskRepository.findAllByOrderByIdAsc()) {
			cronExpressionValidator.validate(task.getCronExpression());
		}
	}

	private TaskResponse toResponse(ScheduledTask entity) {
		return TaskResponse.builder().id(entity.getId()).name(entity.getName())
				.cronExpression(entity.getCronExpression()).enabled(entity.isEnabled())
				.actionType(entity.getActionType()).payload(entity.getPayload())
				.lastExecutedAt(entity.getLastExecutedAt()).executionCount(entity.getExecutionCount())
				.version(entity.getVersion()).build();
	}
}
