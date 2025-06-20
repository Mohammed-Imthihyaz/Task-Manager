package com.imthihyaz.taskmanager.controller;

import com.imthihyaz.taskmanager.dto.CustomResponse;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import com.imthihyaz.taskmanager.service.TaskService;
import com.imthihyaz.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<CustomResponse<Task>> createTask(@RequestBody Task task) {

        try{
            Task t= taskService.createTask(task);
            return new ResponseEntity<>(new CustomResponse<>(true,task,"Task Created Succesfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false,null,e.getMessage()),HttpStatus.BAD_REQUEST );
        }
    }

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<Task>>> listTasks() {
        try {
            List<Task> allTask=  taskService.listTasks();
            return new ResponseEntity<>(new CustomResponse<>(true,allTask,"Retrived all available tasks"),HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false,null,e.getMessage()),HttpStatus.NOT_FOUND );
        }

    }

    @PostMapping("/assign")
    public ResponseEntity<CustomResponse<Task>> assignTask(@RequestParam Long taskId, @RequestParam Long userId) {
        try{
            User user = userService.getUserById(userId);
            Task t = taskService.assignTask(taskId, user);
            return new ResponseEntity<>(new CustomResponse<>(true,t,"Assigned task to user"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false,null,e.getMessage()),HttpStatus.NOT_FOUND );
        }

    }

    @PostMapping("/reassign")
    public ResponseEntity<CustomResponse<Task>>  reassignTask(@RequestParam Long taskId, @RequestParam Long userId) {
        try{
            User user = userService.getUserById(userId);
            Task t= taskService.reassignTask(taskId, user);
           return new ResponseEntity<>(new CustomResponse<>(true,t,"Re-assigned to the user"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false,null,e.getMessage()),HttpStatus.NOT_FOUND );
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CustomResponse<Boolean>> deleteTask(@RequestParam Long taskId){
        try{
            taskService.deleteTask(taskId);
            return new ResponseEntity<>(new CustomResponse<>(true,true,"Task Deleted successfully"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false, null, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getallassigendtasks")
    public  ResponseEntity<CustomResponse<List<Task>>> getAllAssigendTask(){
        try{
            List<Task> tasks =taskService.getAllAssigedTasks();
            return new ResponseEntity<>(new CustomResponse<>(true,tasks,"Fetched all the assigend taks"),HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomResponse<>(false,null,e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }
}