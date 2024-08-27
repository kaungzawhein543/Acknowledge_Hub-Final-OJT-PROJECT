package com.ace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class PostSchedulerService {

    private final TaskScheduler taskScheduler;
    private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Autowired
    public PostSchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void schedulePost(Integer postId, Runnable task, LocalDateTime dateTime) {
        cancelScheduledPost(postId);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
        scheduledTasks.put(postId, scheduledTask);
    }

    public void cancelScheduledPost(Integer postId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(postId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
    }
}
