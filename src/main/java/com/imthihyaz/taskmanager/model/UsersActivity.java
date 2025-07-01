package com.imthihyaz.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users_activity")
public class UsersActivity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_user_active", nullable = false)
    private boolean isActive;

    @Column(name = "user_start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "user_end_time")
    private LocalDateTime endTime;
}