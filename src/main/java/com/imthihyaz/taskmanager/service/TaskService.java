package com.imthihyaz.taskmanager.service;

import com.imthihyaz.taskmanager.dao.TaskRepository;
import com.imthihyaz.taskmanager.dao.UserActivityRepository;
import com.imthihyaz.taskmanager.dto.TaskDto;
import com.imthihyaz.taskmanager.dto.UserActivityDto;
import com.imthihyaz.taskmanager.exceptions.TaskExceptions;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import com.imthihyaz.taskmanager.model.UsersActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private UserActivityRepository userActivityRepository;

    @CachePut(value = "tasks", key = "#result.taskId")
    @CacheEvict(value = "taskList", allEntries = true)
    public Task createTask(TaskDto taskDto) {
        validateTaskDto(taskDto);

        log.debug("Creating task: {}", taskDto.getTaskName());

        Task task = Task.builder()
                .taskDescription(taskDto.getTaskDescription())
                .taskName(taskDto.getTaskName())
                .build();
        Task createdTask = taskRepository.save(task);
        log.info("Task created: {}", createdTask);
        return createdTask;
    }

    @CacheEvict(value = {"tasks", "taskList"}, allEntries = true)
    public void deleteTask(UUID taskId) throws Exception {
        log.debug("Deleting task: {}", taskId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.error("No task found by id {}", taskId);
            return new TaskExceptions("Task not found");
        });

        List<UsersActivity> activities = userActivityRepository.findByTask_TaskId(taskId);
        if (activities != null && !activities.isEmpty()) {
            userActivityRepository.deleteAll(activities);
            log.info("Deleted user activities referencing the task: {}", taskId);
        }
        taskRepository.deleteById(taskId);
        log.info("Deleted task: {}", task.getTaskName());
    }

    @Cacheable(value = "tasks", key = "#taskId")
    public Task getTaskById(UUID taskId) throws Exception {
        log.debug("Retrieving task by id: {}", taskId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.error("No task found by id {}", taskId);
            return new TaskExceptions("Task not found");
        });
        log.info("Retrieved task: {}", task.getTaskName());
        return task;
    }

    @Cacheable(value = "taskList")
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

    public List<Task> getAllAssignedTasks() {
        log.debug("Retrieving all assigned tasks from cache");
        List<Task> tasks = listTasks();
        List<Task> assignedTasks = tasks.stream()
                .filter(task -> task.getAssignedTo() != null)
                .collect(Collectors.toList());
        if (assignedTasks.isEmpty()) {
            log.error("No assigned tasks found");
            throw new TaskExceptions("No task is assigned to any user");
        }
        log.info("Found {} assigned tasks", assignedTasks.size());
        return assignedTasks;
    }

    public List<Task> getAllUnassignedTasks() {
        log.debug("Retrieving all unassigned tasks from cache");
        List<Task> tasks = listTasks();
        List<Task> unAssignedTasks = tasks.stream()
                .filter(task -> task.getAssignedTo() == null)
                .collect(Collectors.toList());
        if (unAssignedTasks.isEmpty()) {
            log.error("No unassigned tasks found");
            throw new TaskExceptions("All tasks are assigned");
        }
        log.info("Found {} unassigned tasks", unAssignedTasks.size());
        return unAssignedTasks;
    }

    @CacheEvict(value = {"tasks", "taskList"}, allEntries = true)
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
        userActivityService.addUserActivityTask(userActivityDto);
        log.info("Added to User Activity");
        Task updatedTask = taskRepository.save(task);
        log.info("Assigned task: {} to user: {}", updatedTask.getTaskName(), user.getUsername());
        return updatedTask;
    }

    @CacheEvict(value = {"tasks", "taskList"}, allEntries = true)
    public Task reassignTask(UUID taskId, UUID userId) throws Exception {
        log.debug("Reassigning task: {}", taskId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.error("No task found by id {}", taskId);
            throw new TaskExceptions("Task not found");
        });
        if (task.getAssignedTo() != null) {
            log.debug("Unassigning task from user: {}", task.getAssignedTo().getUsername());
            task.setAssignedTo(null);
            userActivityService.makeUserInActive(taskId);
        }
        return assignTask(taskId, userId);
    }

    private void validateTaskDto(TaskDto taskDto) {
        if (taskDto == null) {
            throw new TaskExceptions("TaskDto cannot be null");
        }
        if (taskDto.getTaskName() == null || taskDto.getTaskName().isEmpty()) {
            throw new TaskExceptions("Task name cannot be null or empty");
        }
        if (taskDto.getTaskDescription() == null || taskDto.getTaskDescription().isEmpty()) {
            throw new TaskExceptions("Task description cannot be null or empty");
        }
    }
}