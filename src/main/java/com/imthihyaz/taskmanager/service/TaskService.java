package com.imthihyaz.taskmanager.service;

import com.imthihyaz.taskmanager.dao.TaskRepository;
import com.imthihyaz.taskmanager.dto.TaskDto;
import com.imthihyaz.taskmanager.dto.UserActivityDto;
import com.imthihyaz.taskmanager.exceptions.TaskExceptions;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserActivityService userActivityService;

    public Task createTask(TaskDto taskDto) {
        log.debug("Creating task: {}", taskDto.getTaskName());
        Task task = Task.builder()
                .taskDescription(taskDto.getTaskDescription())
                .taskName(taskDto.getTaskName())
                .build();
        Task createdTask = taskRepository.save(task);
        log.info("Task created: {}", createdTask);
        return createdTask;
    }

    public List<Task> listTasks() {
        log.debug("Listing all tasks");
        List<Task> tasks = taskRepository.findAll();
        if (tasks.isEmpty()) {
            log.error("No tasks found");
            throw new TaskExceptions("No tasks found");
        }
        log.info("Found {} tasks", tasks.size());
        return tasks;
    }

    public Task assignTask(UUID taskId, UUID userId) {
        log.debug("Assigning task: {} to user: {}", taskId, userId);
        User user = userService.getUserById(userId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.error("No task found by id {}", taskId);
            throw new TaskExceptions("Task not found");
        });
        task.setAssignedTo(user);
        LocalDateTime currentDateTime = LocalDateTime.now();
        task.setLocalDateTime(currentDateTime);
        UserActivityDto userActivityDto = UserActivityDto.builder()
                .task(task)
                .user(user)
                .startTime(currentDateTime)
                .build();
        userActivityService.addUserActivityTask(userActivityDto); // Correct method call
        log.info("Added to User Activity");
        Task updatedTask = taskRepository.save(task);
        log.info("Assigned task: {} to user: {}", updatedTask.getTaskName(), user.getUsername());
        return updatedTask;
    }

    public Task reassignTask(UUID taskId, UUID userId) throws Exception {
        log.debug("Reassigning task: {}", taskId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.error("No task found by id {}", taskId);
            throw new TaskExceptions("Task not found");
        });
        if (task.getAssignedTo() != null) {
            log.debug("Unassigning task from user: {}", task.getAssignedTo().getUsername());
            task.setAssignedTo(null);
        }
        return assignTask(taskId, userId);
    }

    public void deleteTask(UUID taskId) throws Exception {
        log.debug("Deleting task: {}", taskId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.error("No task found by id {}", taskId);
            return new TaskExceptions("Task not found");
        });
        taskRepository.deleteById(taskId);
        log.info("Deleted task: {}", task.getTaskName());
    }

    public List<Task> getAllAssignedTasks() {
        log.debug("Retrieving all assigned tasks");
        List<Task> assignedTasks = taskRepository.findByAssignedToIsNotNull();
        if (assignedTasks.isEmpty()) {
            log.error("No assigned tasks found");
            throw new TaskExceptions("No task is assigned to any user");
        }
        log.info("Found {} assigned tasks", assignedTasks.size());
        return assignedTasks;
    }

    public List<Task> getAllUnassignedTasks() {
        log.debug("Retrieving all unassigned tasks");
        List<Task> unAssignedTasks = taskRepository.findByAssignedToIsNull();
        if (unAssignedTasks.isEmpty()) {
            log.error("No unassigned tasks found");
            throw new TaskExceptions("All tasks are assigned");
        }
        log.info("Found {} unassigned tasks", unAssignedTasks.size());
        return unAssignedTasks;
    }

    public Task getTaskById(UUID taskId) throws Exception {
        log.debug("Retrieving task by id: {}", taskId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.error("No task found by id {}", taskId);
            return new TaskExceptions("Task not found");
        });
        log.info("Retrieved task: {}", task.getTaskName());
        return task;
    }
}