package com.imthihyaz.taskmanager.service;

import com.imthihyaz.taskmanager.dao.TaskRepository;
import com.imthihyaz.taskmanager.dao.UserRepository;
import com.imthihyaz.taskmanager.dto.UserDto;
import com.imthihyaz.taskmanager.exceptions.UsersException;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    @CachePut(value = "users", key = "#result.userId")
    public User createUser(UserDto userDto) {
        User user = User.builder()
                .userId(userDto.getUserId())
                .username(userDto.getName())
                .build();
        User savedUser = userRepository.save(user);
        log.info("Created user: {}", savedUser);
        return savedUser;
    }

    @Cacheable(value = "users")
    public List<User> listUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        log.info("Fetched all users: {}", users);
        return users;
    }

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(UUID userId) {
        log.info("Fetching user by id: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("No user found by id {}", userId);
            return new UsersException("User not found");
        });
    }

    @Cacheable(value = "userTasks", key = "#userId")
    public List<Task> getUserTask(UUID userId) throws Exception {
        log.info("Fetching tasks for user with id: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UsersException("User Not found"));
        List<Task> allTasksOfUser = new ArrayList<>();
        List<Task> allTask = taskRepository.findAll();
        for (Task t : allTask) {
            if (t.getAssignedTo() != null && Objects.equals(t.getAssignedTo().getUserId(), user.getUserId()))
                allTasksOfUser.add(t);
        }
        if (allTasksOfUser.isEmpty()) {
            log.error("No task is assigned to the user with id {}", userId);
            throw new UsersException("No task is assigned to the user");
        }
        log.info("Fetched tasks for user {}: {}", userId, allTasksOfUser);
        return allTasksOfUser;
    }

    @CacheEvict(value = "users", key = "#userId")
    public void deleteUser(UUID userId) throws Exception {
        log.info("Deleting user by id: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("No user found by id {}", userId);
            return new UsersException("User not found");
        });

        userRepository.deleteById(userId);
        log.info("Deleted user: {}", userId);
    }
}