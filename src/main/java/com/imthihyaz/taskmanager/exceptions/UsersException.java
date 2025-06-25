package com.imthihyaz.taskmanager.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UsersException extends RuntimeException {
       private String message;
}
