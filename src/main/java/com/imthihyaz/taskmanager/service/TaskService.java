package com.imthihyaz.taskmanager.service;


import com.imthihyaz.taskmanager.dao.TaskRepository;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) throws Exception {
      List<Task> list = taskRepository.findAll();
        for (Task value : list) {
            if(value.getTaskName().equals(task.getTaskName())) throw new Exception("Same task exists");
        }
        return taskRepository.save(task);
    }

    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    public Task assignTask(Long taskId, User user) throws Exception{
        Task task = taskRepository.findById(taskId).orElseThrow(()->new Exception("Task Not found"));
        task.setAssignedTo(user);
        return taskRepository.save(task);
    }

    public Task reassignTask(Long taskId, User user) throws Exception {
        return assignTask(taskId, user);
    }

    public void deleteTask(Long taskId) throws Exception {
        Task t =taskRepository.findById(taskId).orElseThrow(()->new Exception("Task not found"));
        taskRepository.deleteById(taskId);
        System.out.println("Deleted successfully");
    }

    public List<Task> getAllAssigedTasks  () throws Exception {
            List<Task> assignedTasks =new ArrayList<>();
            List<Task> allTasks =taskRepository.findAll();
            for(Task t:allTasks){
                if(t.getAssignedTo() !=null)assignedTasks.add(t);
            }
            if(assignedTasks.isEmpty()){
                throw new Exception("NO task is assgined to any user");
            }
            return assignedTasks;
    }
}