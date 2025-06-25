package com.imthihyaz.taskmanager.customResponse;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CustomResponse<T>{
    private T data;
    private String message;

}
