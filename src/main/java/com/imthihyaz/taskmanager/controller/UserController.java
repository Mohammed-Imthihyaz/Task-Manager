package com.imthihyaz.taskmanager.controller;


import com.imthihyaz.taskmanager.dto.CustomResponse;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import com.imthihyaz.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<CustomResponse<User>> createUser(@RequestBody User user) {
        try{
           User uxser= userService.createUser(user);
           return new ResponseEntity<>(new CustomResponse<>(true,uxser,"User created Succesfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false, null, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<User>>> listUsers() {
        try{
            List<User> allUsers =userService.listUsers();
            return new ResponseEntity<>(new CustomResponse<>(true,allUsers,"Retrived all the Users"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false,null,e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-user-task")
    public ResponseEntity<CustomResponse<List<Task>>> getUserTask(@RequestParam Long userId) {
        try {
            List<Task> userTask =userService.getUserTask(userId);
            System.out.println(userTask);
            return  new ResponseEntity<>(new CustomResponse<>(true,userTask,"Fetched all tasks for the user"),HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false,null,e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
}