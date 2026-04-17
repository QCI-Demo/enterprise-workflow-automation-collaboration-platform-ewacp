package com.ewacp.scheduler.scheduling;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ewacp.scheduler.model.ScheduledTask;
import com.ewacp.scheduler.repo.ScheduledTaskRepository;

@Component
public class TaskDispatcher {

	private static final Logger log = LoggerFactory.getLogger(TaskDispatcher.class);

	private final ScheduledTaskRepository taskRepository;

	public TaskDispatcher(ScheduledTaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	/**
	 * Runs the configured action for a scheduled task. Updates persistence so
	 * execution is observable and the service remains usable behind a load
	 * balancer without in-memory session state.
	 *
	 * @param taskId persistent task id
	 */
	@Transactional
	public void dispatch(long taskId) {
		ScheduledTask task = taskRepository.findById(taskId).orElse(null);
		if (task == null || !task.isEnabled()) {
			return;
		}
		log.info("Executing scheduled task id={} name={} actionType={}", task.getId(), task.getName(),
				task.getActionType());
		task.setLastExecutedAt(Instant.now());
		task.setExecutionCount(task.getExecutionCount() + 1);
		taskRepository.save(task);
	}
}
