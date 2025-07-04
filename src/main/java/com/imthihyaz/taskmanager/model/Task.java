package com.imthihyaz.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
@Builder
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID taskId;

    @Column(name = "task_name")
    private String taskName;

    @Column(length = 1024)
    private String taskDescription;

    @ManyToOne
    @JoinColumn(name ="assigned_to")
    private User assignedTo;

    @Column(name="user_assigned_time")
    private LocalDateTime localDateTime;
}