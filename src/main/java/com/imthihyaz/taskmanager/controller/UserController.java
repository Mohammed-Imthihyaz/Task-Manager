package com.imthihyaz.taskmanager.controller;

import com.imthihyaz.taskmanager.customResponse.CustomResponse;
import com.imthihyaz.taskmanager.dto.UserDto;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import com.imthihyaz.taskmanager.model.UsersActivity;
import com.imthihyaz.taskmanager.service.UserActivityService;
import com.imthihyaz.taskmanager.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserActivityService userActivityService;

    @PostMapping
    public ResponseEntity<CustomResponse<User>> createUser(@RequestBody UserDto userdto) {
        log.info("Creating user with name: {}", userdto.getName());
        try {
            User user = userService.createUser(userdto);
            log.info("User created successfully: {}", user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomResponse<>(user, "User created successfully"));
        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error creating user"));
        }
    }

    @GetMapping()
    public ResponseEntity<CustomResponse<List<User>>> listUsers() {
        log.info("Fetching all users");
        try {
            List<User> allUsers = userService.listUsers();
            log.info("Fetched all users: {}", allUsers);
            return ResponseEntity.ok(new CustomResponse<>(allUsers, "Retrieved all users"));
        } catch (Exception e) {
            log.error("Error fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching users"));
        }
    }

    @GetMapping("/{userId}/tasks")
    public ResponseEntity<CustomResponse<List<Task>>> getUserTasks(@PathVariable UUID userId) {
        log.info("Fetching tasks for user: {}", userId);
        try {
            List<Task> userTasks = userService.getUserTask(userId);
            log.info("Fetched tasks for user: {}", userTasks);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(new CustomResponse<>(userTasks, "Fetched all tasks for the user"));
        } catch (Exception e) {
            log.error("Error fetching user tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching user tasks"));
        }
    }

    @GetMapping("/activities/{userId}/active")
    public ResponseEntity<CustomResponse<UsersActivity>> getActiveUser(@PathVariable UUID userId) {
        log.info("Fetching active user: {}", userId);
        try {
            Optional<UsersActivity> userActivity = userActivityService.getActiveUserById(userId);
            if (userActivity.isPresent()) {
                log.info("Fetched active user: {}", userActivity.get());
                return ResponseEntity.status(HttpStatus.FOUND)
                        .body(new CustomResponse<>(userActivity.get(), "Fetched active user"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CustomResponse<>(null, "Active user not found"));
            }
        } catch (Exception e) {
            log.error("Error fetching active user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching active user"));
        }
    }
}