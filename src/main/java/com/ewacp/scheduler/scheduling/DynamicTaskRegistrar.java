package com.ewacp.scheduler.scheduling;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.stereotype.Component;

import com.ewacp.scheduler.event.ScheduledTasksChangedEvent;
import com.ewacp.scheduler.model.ScheduledTask;
import com.ewacp.scheduler.repo.ScheduledTaskRepository;
import com.ewacp.scheduler.service.TaskService;

import jakarta.annotation.PostConstruct;

@Component
public class DynamicTaskRegistrar {

	private final TaskScheduler taskScheduler;
	private final TaskDispatcher taskDispatcher;
	private final ScheduledTaskRepository taskRepository;
	private final TaskService taskService;

	private final Map<Long, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

	public DynamicTaskRegistrar(TaskScheduler taskScheduler, TaskDispatcher taskDispatcher,
			ScheduledTaskRepository taskRepository, TaskService taskService) {
		this.taskScheduler = taskScheduler;
		this.taskDispatcher = taskDispatcher;
		this.taskRepository = taskRepository;
		this.taskService = taskService;
	}

	@PostConstruct
	void registerOnStartup() {
		refreshAll();
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	void onTasksChanged(ScheduledTasksChangedEvent event) {
		refreshAll();
	}

	private void refreshAll() {
		taskService.validateAllCronExpressions();
		scheduled.values().forEach(f -> f.cancel(false));
		scheduled.clear();
		for (ScheduledTask task : taskRepository.findAllByEnabledTrueOrderByIdAsc()) {
			if (task == null) {
				continue;
			}
			Long taskId = task.getId();
			String cron = task.getCronExpression();
			if (taskId == null || cron == null) {
				continue;
			}
			CronTrigger trigger = new CronTrigger(cron);
			ScheduledFuture<?> future = taskScheduler.schedule(() -> taskDispatcher.dispatch(taskId.longValue()),
					trigger);
			if (future != null) {
				scheduled.put(taskId, future);
			}
		}
	}
}
