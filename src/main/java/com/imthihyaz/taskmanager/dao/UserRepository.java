package com.imthihyaz.taskmanager.dao;


import com.imthihyaz.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {
}