package com.imthihyaz.taskmanager.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TaskDto {

    private UUID taskId;
    private String taskName;
    private String taskDescription;
    private UserDto userDto;
}
