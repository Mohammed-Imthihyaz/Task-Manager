package com.imthihyaz.taskmanager.service;


import com.imthihyaz.taskmanager.dao.TaskRepository;
import com.imthihyaz.taskmanager.dao.UserRepository;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Task> getUserTask(Long userId) throws Exception {
        User user =userRepository.findById(userId).orElseThrow(()->new Exception("User Not found"));
        List<Task> allTasksOfUser = new ArrayList<>();
        List<Task> allTask =taskRepository.findAll();
        for(Task t:allTask){
            if(t.getAssignedTo()!=null && Objects.equals(t.getAssignedTo().getUserId(), user.getUserId()))allTasksOfUser.add(t);
        }
        System.out.println(allTask+" "+allTasksOfUser);
        if(allTasksOfUser.isEmpty())throw new Exception("No task is assigend to the user");
        return allTasksOfUser;
    }
}