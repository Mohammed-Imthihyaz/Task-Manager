package com.imthihyaz.taskmanager.controller;

import com.imthihyaz.taskmanager.customResponse.CustomResponse;
import com.imthihyaz.taskmanager.model.UsersActivity;
import com.imthihyaz.taskmanager.service.UserActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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




}
