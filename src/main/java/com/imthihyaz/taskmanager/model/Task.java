package com.imthihyaz.taskmanager.model;

import com.imthihyaz.taskmanager.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String taskName;
    private String taskDescription;

    @ManyToOne
    @JoinColumn(name ="assigned_to")
    private User assignedTo;
}