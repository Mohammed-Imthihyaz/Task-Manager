package com.imthihyaz.taskmanager.dao;


import com.imthihyaz.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}