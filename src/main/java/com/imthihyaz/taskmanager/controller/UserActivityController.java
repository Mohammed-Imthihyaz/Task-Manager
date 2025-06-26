package com.imthihyaz.taskmanager.controller;

import com.imthihyaz.taskmanager.customResponse.CustomResponse;
import com.imthihyaz.taskmanager.model.UsersActivity;
import com.imthihyaz.taskmanager.service.UserActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-activity")
@Slf4j
//get all active tasks
//get all inactive tasks
//get a particular task which is active / inactive

public class UserActivityController {
    @Autowired
    UserActivityService userActivityService;

    @GetMapping()
    public ResponseEntity<CustomResponse<List<UsersActivity>>> getAllActiveTasks(){
        try {
            log.info("Fetching all the active users");
            List<UsersActivity> usersActivities =userActivityService.getAllActiveTasks();
            return new ResponseEntity<>(new CustomResponse<>(usersActivities,"Fetched all active users task"), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(null,e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<CustomResponse<UsersActivity>> getActiveTaskById(@PathVariable UUID taskId){
        log.info("The id give is "+taskId);
        try{
            UsersActivity usersActivity =userActivityService.getActiveTaskById(taskId);
            log.info("Fetched successfully");
            return new ResponseEntity<>(new CustomResponse<>(usersActivity,"Fetched successfully"),HttpStatus.FOUND);

        } catch (Exception e) {
            log.error("Something went wrong ");
            return new ResponseEntity<>(new CustomResponse<>(null,"Something Went wrong"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
