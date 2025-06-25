package com.imthihyaz.taskmanager.service;

import com.imthihyaz.taskmanager.dao.UserActivityRepository;
import com.imthihyaz.taskmanager.dto.UserActivityDto;
import com.imthihyaz.taskmanager.exceptions.UserActivityException;
import com.imthihyaz.taskmanager.model.UsersActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    public void addUserActivityTask(UserActivityDto userActivityDto) {
        log.info("Creating a userActivity class");
        UsersActivity usersActivity = UsersActivity.builder()
                .user(userActivityDto.getUser())
                .isActive(true)
                .task(userActivityDto.getTask())
                .startTime(userActivityDto.getStartTime())
                .build();
        userActivityRepository.save(usersActivity);
        log.info("UserActivity saved successfully");
    }

    public List<UsersActivity> getAllActiveTasks() {
        log.info("Fetching all the active activities");
        List<UsersActivity> usersActivities = userActivityRepository.findByIsActiveTrue();
        if (usersActivities.isEmpty()) {
            log.error("No active users found");
            throw new UserActivityException("No active users");
        }
        log.info("Found all active tasks " + usersActivities);
        return usersActivities;
    }

    public UsersActivity getActiveTaskById(UUID taskId) {
        UsersActivity task = userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId)
                .orElseThrow(() -> {
                    log.error("No active task found");
                    return new UserActivityException("No active task found");
                });
        return task;
    }
}