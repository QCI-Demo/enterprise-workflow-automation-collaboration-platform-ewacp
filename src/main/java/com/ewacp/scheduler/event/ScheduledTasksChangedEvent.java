package com.ewacp.scheduler.event;

import org.springframework.context.ApplicationEvent;

public class ScheduledTasksChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	public ScheduledTasksChangedEvent(Object source) {
		super(source);
	}
}
