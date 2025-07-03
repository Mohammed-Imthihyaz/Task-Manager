package com.imthihyaz.taskmanager;

import com.imthihyaz.taskmanager.dto.TaskDto;
import com.imthihyaz.taskmanager.exceptions.TaskExceptions;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	TaskService taskService;

	@Test
	void createTaskTest_Success() {
		TaskDto task = TaskDto.builder()
				.taskName("task1")
				.taskDescription("task1 description")
				.build();
		Task actualResponse = taskService.createTask(task);
		actualResponse.setTaskId(null); // To ignore auto-generated IDs for comparison

		Task expectedResponse = Task.builder()
				.taskName("task1")
				.taskDescription("task1 description")
				.build();

		assertEquals(expectedResponse, actualResponse, "The task should have been created successfully");
	}

	@Test
	void createTaskTest_Failure_MissingName() {

		TaskDto task = TaskDto.builder()
				.taskDescription("task1 description")
				.build();

		assertThrows(TaskExceptions.class, () -> {
			taskService.createTask(task);
		}, "Creating a task without a name should throw a TaskExceptions");
	}

	@Test
	void createTaskTest_Failure_MissingDescription() {
		TaskDto task = TaskDto.builder()
				.taskName("task2")
				.build();

		assertThrows(TaskExceptions.class, () -> {
			taskService.createTask(task);
		}, "Creating a task without a description should throw a TaskExceptions");
	}


	@Test
	void createTaskTest_Failure_NullTask() {
		assertThrows(TaskExceptions.class, () -> {
			taskService.createTask(null);
		}, "Creating a task with a null payload should throw a TaskExceptions");
	}


	@Test
	void deleteTaskTest_Success(){

	}

}