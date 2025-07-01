package com.imthihyaz.taskmanager.service;

import com.imthihyaz.taskmanager.dao.UserActivityRepository;
import com.imthihyaz.taskmanager.dto.UserActivityDto;
import com.imthihyaz.taskmanager.exceptions.UserActivityException;
import com.imthihyaz.taskmanager.model.UsersActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @CachePut(value = "userActivities", key = "#result.id")
    public void addUserActivityTask(UserActivityDto userActivityDto) {
        log.info("Creating a userActivity class");
        Optional<UsersActivity> user = getActiveUserById(userActivityDto.getUser().getUserId());
        if (user.isPresent()) {
            log.info("Some user is active, making it inactive ");
            user.get().setActive(false);
            user.get().setEndTime(LocalDateTime.now());
            this.saveOrUpdateUserActivity(user.get());
        } else {
            log.info("No user is active for this task");
        }
        UsersActivity usersActivity = UsersActivity.builder()
                .user(userActivityDto.getUser())
                .isActive(true)
                .task(userActivityDto.getTask())
                .startTime(userActivityDto.getStartTime())
                .build();
        this.saveOrUpdateUserActivity(usersActivity);
        log.info("UserActivity saved successfully");
    }

    @CachePut(value = "userActivities", key = "#usersActivity.id")
    public void saveOrUpdateUserActivity(UsersActivity usersActivity) {
        log.info("Saving or updating user activity");
        userActivityRepository.save(usersActivity);
        log.info("User activity saved or updated successfully");
    }

    @CacheEvict(value = "userActivities", key = "#taskId")
    public void makeUserInActive(UUID taskId) {
        log.info("make user inactive for task {}", taskId);
        UsersActivity usersActivity = userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId).orElseThrow(() -> {
            log.error("No active task found for task heheh {}", taskId);
            throw new UserActivityException("No active task found");
        });
        usersActivity.setActive(false);
        usersActivity.setEndTime(LocalDateTime.now());
        log.info("Setting activity to false for task {}", taskId);
        this.saveOrUpdateUserActivity(usersActivity);
    }

    @Cacheable(value = "userActivities", key = "activeTasks")
    public List<UsersActivity> getAllActiveTasks() {
        log.info("Fetching all the active activities");
        List<UsersActivity> usersActivities = userActivityRepository.findByIsActiveTrue();
        if (usersActivities.isEmpty()) {
            log.error("No active users found");
            throw new UserActivityException("No active users");
        }
        log.info("Found all active tasks: {}", usersActivities);
        return usersActivities;
    }

    @Cacheable(value = "userActivities", key = "#taskId")
    public UsersActivity getActiveTaskById(UUID taskId) {
        return userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId).orElseThrow(() -> {
            log.error("No active task found for task {}", taskId);
            throw new UserActivityException("No active task found");
        });
    }

    @Cacheable(value = "userActivities", key = "#userId")
    public Optional<UsersActivity> getActiveUserById(UUID userId) {
        return userActivityRepository.findByUser_UserIdAndIsActiveTrue(userId);
    }

    @Cacheable(value = "userActivities", key = "#taskId")
    public List<UsersActivity> allInActiveUsersOfTask(UUID taskId) {
        List<UsersActivity> usersActivityList = userActivityRepository.findByTask_TaskIdAndIsActiveFalse(taskId);
        if (usersActivityList.isEmpty()) {
            log.info("No previous users for the task");
            throw new UserActivityException("No previous User");
        }
        log.info("Found inactive users for the task: {}", usersActivityList);
        return usersActivityList;
    }
}