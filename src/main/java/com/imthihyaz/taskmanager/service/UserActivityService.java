package com.imthihyaz.taskmanager.service;

import com.imthihyaz.taskmanager.dao.UserActivityRepository;
import com.imthihyaz.taskmanager.dto.UserActivityDto;
import com.imthihyaz.taskmanager.exceptions.UserActivityException;
import com.imthihyaz.taskmanager.model.UsersActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    public void addUserActivityTask(UserActivityDto userActivityDto) {
        log.info("Creating a userActivity class");
      Optional<UsersActivity> user =userActivityRepository.findByTask_TaskIdAndIsActiveTrue(userActivityDto.getTask().getTaskId());
      if(user.isEmpty()){
          log.info("No user is active for this task");
      }else{
          log.info("Some user is active, making it inactive ");
          makeUserInActive(user.get().getTask().getTaskId());
      }
        UsersActivity usersActivity = UsersActivity.builder()
                .user(userActivityDto.getUser())
                .isActive(true)
                .task(userActivityDto.getTask())
                .startTime(userActivityDto.getStartTime())
                .build();
        userActivityRepository.save(usersActivity);
        log.info("UserActivity saved successfully");
    }

    public void makeUserInActive(UUID taskId) {
        log.info("make user inactive for task {}", taskId);
        UsersActivity usersActivity = userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId).orElseThrow(() -> {
            log.error("No active task found for task {}", taskId);
            return new UserActivityException("No active task found");
        });
        usersActivity.setActive(false);
        log.info("Setting activity to false for task {}", taskId);
        userActivityRepository.save(usersActivity);
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
        UsersActivity task = userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId).orElseThrow(() -> {
            log.error("No active task found for task {}", taskId);
            return new UserActivityException("No active task found");
        });
        return task;
    }
}