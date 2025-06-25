package com.imthihyaz.taskmanager.controller;

import com.imthihyaz.taskmanager.customResponse.CustomResponse;
import com.imthihyaz.taskmanager.dto.TaskDto;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping()
    public ResponseEntity<CustomResponse<Task>> createTask(@RequestBody TaskDto taskDto) {
        log.info("The task name is " + taskDto.getTaskName());
        try {
            Task task = taskService.createTask(taskDto);
            log.info("Task created successfully " + task);
            return new ResponseEntity<>(new CustomResponse<>(task, "Task Created Successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating task", e);
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<Task>>> listTasks() {
        try {
            List<Task> allTasks = taskService.listTasks();
            log.info("Retrieved all the tasks " + allTasks);
            return new ResponseEntity<>(new CustomResponse<>(allTasks, "Retrieved all available tasks"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving tasks", e);
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<CustomResponse<Task>> getTaskById(@PathVariable UUID taskId) {
        try {
            Task task = taskService.getTaskById(taskId);
            log.info("Fetched the task " + task);
            return new ResponseEntity<>(new CustomResponse<>(task, "Fetched successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching task", e);
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<CustomResponse<Task>> assignTask(@RequestParam UUID taskId, @RequestParam UUID userId) {
        try {
            Task t = taskService.assignTask(taskId, userId);
            log.info("Assigned task to the user " + t);
            return new ResponseEntity<>(new CustomResponse<>(t, "Assigned task to user"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error assigning task", e);
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/re-assign")
    public ResponseEntity<CustomResponse<Task>> reassignTask(@RequestParam UUID taskId, @RequestParam UUID userId) {
        try {
            Task t = taskService.reassignTask(taskId, userId);
            log.info("Re-assigned task to the user " + t);
            return new ResponseEntity<>(new CustomResponse<>(t, "Re-assigned to the user"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error reassigning task", e);
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<CustomResponse<Boolean>> deleteTask(@RequestParam UUID taskId) {
        try {
            taskService.deleteTask(taskId);
            log.info("Deleted the task");
            return new ResponseEntity<>(new CustomResponse<>(true, "Task Deleted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting task", e);
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-assigned-tasks")
    public ResponseEntity<CustomResponse<List<Task>>> getAllAssignedTasks() {
        try {
            List<Task> tasks = taskService.getAllAssignedTasks();
            log.info("Fetched all the assigned tasks " + tasks);
            return new ResponseEntity<>(new CustomResponse<>(tasks, "Fetched all the assigned tasks"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching assigned tasks", e);
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all-unassigned-tasks")
    public ResponseEntity<CustomResponse<List<Task>>> allUnAssignedTasks() {
        try {
            List<Task> tasks = taskService.getAllUnassignedTasks();
            log.info("Fetched all the Un-Assigned tasks " + tasks);
            return new ResponseEntity<>(new CustomResponse<>(tasks, "Fetched all the unassigned tasks"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching unassigned tasks", e);
            return new ResponseEntity<>(new CustomResponse<>(null, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}