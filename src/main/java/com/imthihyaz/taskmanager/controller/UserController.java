package com.imthihyaz.taskmanager.controller;


import com.imthihyaz.taskmanager.customResponse.CustomResponse;
import com.imthihyaz.taskmanager.dto.UserDto;

import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import com.imthihyaz.taskmanager.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<CustomResponse<User>> createUser(@RequestBody UserDto userdto) {
        log.info("The user name is "+ userdto.getName());
        try{
           User user= userService.createUser(userdto);
           log.info("User created successfully "+user);
           return new ResponseEntity<>(new CustomResponse<>(user,"User created Successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<User>>> listUsers() {
        log.info("Fetching all the Users");
        try{
            List<User> allUsers =userService.listUsers();
            log.info("Fetched all the users"+ allUsers);
            return new ResponseEntity<>(new CustomResponse<>(allUsers,"Retrived all the Users"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(null,e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user-task")
    public ResponseEntity<CustomResponse<List<Task>>> getUserTask(@RequestParam UUID userId) {
        try {
            List<Task> userTask =userService.getUserTask(userId);
           log.info("Fetched all the user task");
            return  new ResponseEntity<>(new CustomResponse<>(userTask,"Fetched all tasks for the user"),HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(null,e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }
}