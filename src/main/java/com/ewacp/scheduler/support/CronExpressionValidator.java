package com.ewacp.scheduler.support;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

@Component
public class CronExpressionValidator {

	/**
	 * Validates a six-field Spring cron expression (seconds through optional zone handled by parser).
	 *
	 * @param cronExpression cron string
	 * @throws IllegalArgumentException if invalid
	 */
	public void validate(String cronExpression) {
		try {
			CronExpression.parse(cronExpression);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid cron expression: " + cronExpression, ex);
		}
	}
}
