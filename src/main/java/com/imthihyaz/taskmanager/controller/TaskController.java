package com.imthihyaz.taskmanager.controller;

import com.imthihyaz.taskmanager.customResponse.CustomResponse;
import com.imthihyaz.taskmanager.dto.TaskDto;
import com.imthihyaz.taskmanager.exceptions.TaskExceptions;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.UsersActivity;
import com.imthihyaz.taskmanager.service.TaskService;
import com.imthihyaz.taskmanager.service.UserActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserActivityService userActivityService;

    @PostMapping
    public ResponseEntity<CustomResponse<Task>> createTask(@RequestBody TaskDto taskDto) {
        log.info("Creating task with name: {}", taskDto.getTaskName());
        try {
            Task task = taskService.createTask(taskDto);
            log.info("Task created successfully: {}", task);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomResponse<>(task, "Task created successfully"));
        } catch (Exception e) {
            log.error("Error creating task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error creating task"));
        }
    }

    @GetMapping()
    public ResponseEntity<CustomResponse<List<Task>>> listTasks() {
        try {
            List<Task> allTasks = taskService.listTasks();
            log.info("Retrieved all tasks: {}", allTasks);
            return ResponseEntity.ok(new CustomResponse<>(allTasks, "Retrieved all tasks"));
        } catch (Exception e) {
            log.error("Error retrieving tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error retrieving tasks"));
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<CustomResponse<Task>> getTaskById(@PathVariable UUID taskId) {
        try {
            Task task = taskService.getTaskById(taskId);
            log.info("Fetched task: {}", task);
            return ResponseEntity.ok(new CustomResponse<>(task, "Fetched task successfully"));
        } catch (Exception e) {
            log.error("Error fetching task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching task"));
        }
    }

    @PostMapping("/{taskId}/assign")
    public ResponseEntity<CustomResponse<Task>> assignTask(@PathVariable UUID taskId, @RequestParam UUID userId) {
        try {
            Task task = taskService.assignTask(taskId, userId);
            log.info("Assigned task to user: {}", task);
            return ResponseEntity.ok(new CustomResponse<>(task, "Assigned task to user"));
        } catch (Exception e) {
            log.error("Error assigning task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error assigning task"));
        }
    }

    @PostMapping("/{taskId}/reassign")
    public ResponseEntity<CustomResponse<Task>> reassignTask(@PathVariable UUID taskId, @RequestParam UUID userId) {
        try {
            Task task = taskService.reassignTask(taskId, userId);
            log.info("Reassigned task to user: {}", task);
            return ResponseEntity.ok(new CustomResponse<>(task, "Reassigned task to user"));
        } catch (Exception e) {
            log.error("Error reassigning task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error reassigning task"));
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<CustomResponse<Boolean>> deleteTask(@PathVariable UUID taskId) {
        try {
            taskService.deleteTask(taskId);
            log.info("Deleted task: {}", taskId);
            return ResponseEntity.ok(new CustomResponse<>(true, "Task deleted successfully"));
        } catch (TaskExceptions e) {
            log.error("Error deleting task due to reference constraint", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CustomResponse<>(null, "Error deleting task due to reference constraint"));
        } catch (Exception e) {
            log.error("Error deleting task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error deleting task"));
        }
    }

    @GetMapping("/assigned")
    public ResponseEntity<CustomResponse<List<Task>>> getAllAssignedTasks() {
        try {
            List<Task> tasks = taskService.getAllAssignedTasks();
            log.info("Fetched all assigned tasks: {}", tasks);
            return ResponseEntity.ok(new CustomResponse<>(tasks, "Fetched all assigned tasks"));
        } catch (Exception e) {
            log.error("Error fetching assigned tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching assigned tasks"));
        }
    }

    @GetMapping("/unassigned")
    public ResponseEntity<CustomResponse<List<Task>>> getAllUnassignedTasks() {
        try {
            List<Task> tasks = taskService.getAllUnassignedTasks();
            log.info("Fetched all unassigned tasks: {}", tasks);
            return ResponseEntity.ok(new CustomResponse<>(tasks, "Fetched all unassigned tasks"));
        } catch (Exception e) {
            log.error("Error fetching unassigned tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching unassigned tasks"));
        }
    }

    @GetMapping("/activities/active")
    public ResponseEntity<CustomResponse<List<UsersActivity>>> getAllActiveTasks() {
        try {
            List<UsersActivity> usersActivities = userActivityService.getAllActiveTasks();
            log.info("Fetched all active tasks: {}", usersActivities);
            return ResponseEntity.ok(new CustomResponse<>(usersActivities, "Fetched all active tasks"));
        } catch (Exception e) {
            log.error("Error fetching active tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching active tasks"));
        }
    }

    @GetMapping("/activities/{taskId}/active")
    public ResponseEntity<CustomResponse<UsersActivity>> getActiveTaskById(@PathVariable UUID taskId) {
        try {
            Optional<UsersActivity> usersActivity = userActivityService.getActiveTaskById(taskId);
            log.info("Fetched active task: {}", usersActivity);
            return ResponseEntity.ok(new CustomResponse<>(usersActivity.get(), "Fetched active task successfully"));
        } catch (Exception e) {
            log.error("Error fetching active task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching active task"));
        }
    }

    @GetMapping("/activities/{taskId}/inactive-users")
    public ResponseEntity<CustomResponse<List<UsersActivity>>> getAllInactiveUsersOfTask(@PathVariable UUID taskId) {
        try {
            List<UsersActivity> allInactiveUsers = userActivityService.allInActiveUsersOfTask(taskId);
            log.info("Fetched all inactive users of task: {}", allInactiveUsers);
            return ResponseEntity.ok(new CustomResponse<>(allInactiveUsers, "Fetched all inactive users of task"));
        } catch (Exception e) {
            log.error("Error fetching inactive users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching inactive users"));
        }
    }
    @GetMapping("activities/{userId}/user-task")
    public ResponseEntity<CustomResponse<List<UsersActivity>>> getAllTaskOfUser(@PathVariable UUID userId){
        try{
            List<UsersActivity> allTaskOfUser =userActivityService.getAllAssignedUserTasks(userId);
            log.info("Fetched all inactive users of task: {}", allTaskOfUser);
            return ResponseEntity.ok(new CustomResponse<>(allTaskOfUser, "Fetched all tasks of a user"));
        } catch (Exception e) {
            log.error("Error fetching tasks of a users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse<>(null, "Error fetching tasks of a users"));
        }
    }

}