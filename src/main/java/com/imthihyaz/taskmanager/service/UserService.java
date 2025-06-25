package com.imthihyaz.taskmanager.service;


import com.imthihyaz.taskmanager.dao.TaskRepository;
import com.imthihyaz.taskmanager.dao.UserRepository;
import com.imthihyaz.taskmanager.dto.UserDto;
import com.imthihyaz.taskmanager.exceptions.UsersException;

import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public User createUser(UserDto userDto) {
        User user = User.builder()
                .userId(userDto.getUserId())
                .username(userDto.getName())
                .build();
        return userRepository.save(user);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Their is no user by the id "+userId);
           return new UsersException("User not found");
        });
    }

    public List<Task> getUserTask(UUID userId) throws Exception {
        User user =userRepository.findById(userId).orElseThrow(()->new UsersException("User Not found"));
        List<Task> allTasksOfUser = new ArrayList<>();
        List<Task> allTask =taskRepository.findAll();
        for(Task t:allTask){
            if(t.getAssignedTo()!=null && Objects.equals(t.getAssignedTo().getUserId(), user.getUserId()))allTasksOfUser.add(t);
        }
        System.out.println(allTask+" "+allTasksOfUser);
        if(allTasksOfUser.isEmpty()){
            log.error("No task is assigned to the user with id "+userId);
            throw new UsersException("No task is assigned to the user");
        }
        return allTasksOfUser;
    }
}