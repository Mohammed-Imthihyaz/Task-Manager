package com.imthihyaz.taskmanager.dao;

import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.UsersActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserActivityRepository extends JpaRepository<UsersActivity, UUID> {
    List<UsersActivity> findByIsActiveTrue();
    Optional<UsersActivity> findByTask_TaskIdAndIsActiveTrue(UUID taskId);
}
