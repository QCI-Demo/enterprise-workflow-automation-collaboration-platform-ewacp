package com.ewacp.scheduler.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ewacp.scheduler.model.ScheduledTask;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {

	List<ScheduledTask> findAllByOrderByIdAsc();

	List<ScheduledTask> findAllByEnabledTrueOrderByIdAsc();
}
