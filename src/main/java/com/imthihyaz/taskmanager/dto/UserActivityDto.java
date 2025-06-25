package com.imthihyaz.taskmanager.dto;

import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserActivityDto {
    private UUID id;
    private Task task;
    private User user;
    private boolean isActive;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
