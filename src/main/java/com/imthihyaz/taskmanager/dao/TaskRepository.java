package com.imthihyaz.taskmanager.dao;


import com.imthihyaz.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}