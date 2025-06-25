package com.imthihyaz.taskmanager.dto;

import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDto {

    private UUID userId;
    private String name;
}
