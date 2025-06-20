package com.imthihyaz.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse<T>{
    private boolean success;
    private T data;
    private String message;

}
