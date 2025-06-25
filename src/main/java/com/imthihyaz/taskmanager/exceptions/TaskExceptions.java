package com.imthihyaz.taskmanager.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TaskExceptions extends RuntimeException {
   private String message;
}
