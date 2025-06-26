package com.imthihyaz.taskmanager.service;

import com.imthihyaz.taskmanager.dao.UserActivityRepository;
import com.imthihyaz.taskmanager.dto.UserActivityDto;
import com.imthihyaz.taskmanager.exceptions.UserActivityException;
import com.imthihyaz.taskmanager.model.UsersActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
          user.get().setActive(false);
          this.saveOrUpdateUserActivity(user.get());
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

    public void saveOrUpdateUserActivity(UsersActivity usersActivity) {
        log.info("Saving or updating user activity");
        userActivityRepository.save(usersActivity);
        log.info("User activity saved or updated successfully");
    }

    public void makeUserInActive(UUID taskId) {
        log.info("make user inactive for task {}", taskId);
        UsersActivity usersActivity = userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId).orElseThrow(() -> {
            log.error("No active task found for task {}", taskId);
            throw new UserActivityException("No active task found");
        });
        usersActivity.setActive(false);
        usersActivity.setEndTime(LocalDateTime.now());
        log.info("Setting activity to false for task {}", taskId);
        this.saveOrUpdateUserActivity(usersActivity);
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
            throw  new UserActivityException("No active task found");
        });
        return task;
    }

    public List<UsersActivity> allInActiveUsersOfTask(UUID taskId){

        List<UsersActivity> usersActivityList=userActivityRepository.findByTask_TaskIdAndIsActiveFalse(taskId);
        if(usersActivityList.isEmpty()){
            log.info("their are not previous users for the task");
            throw  new UserActivityException("No previous User");
        }
        return usersActivityList;
    }

    public String  getStartAndEndTime(UUID userId){
        UsersActivity user =userActivityRepository.findByUser_UserIdAndIsActiveFalse(userId).orElseThrow(()->{
            log.info("No user exists");
            return new UserActivityException("No user such user exists");
        });
        String userTime="The user "+user.getUser().getUsername()+" started the task at "+user.getStartTime()+" and ended at "+user.getEndTime();
        return userTime;
    }

}