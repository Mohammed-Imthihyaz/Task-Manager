package com.imthihyaz.taskmanager.dao;


import com.imthihyaz.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByAssignedToIsNotNull();
    List<Task> findByAssignedToIsNull();
}