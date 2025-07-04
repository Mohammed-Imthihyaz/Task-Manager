package com.imthihyaz.taskmanager;

import com.imthihyaz.taskmanager.dao.UserRepository;
import com.imthihyaz.taskmanager.dao.TaskRepository;
import com.imthihyaz.taskmanager.dao.UserActivityRepository;
import com.imthihyaz.taskmanager.dto.TaskDto;
import com.imthihyaz.taskmanager.dto.UserActivityDto;
import com.imthihyaz.taskmanager.exceptions.TaskExceptions;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import com.imthihyaz.taskmanager.model.UsersActivity;
import com.imthihyaz.taskmanager.service.TaskService;
import com.imthihyaz.taskmanager.service.UserService;
import com.imthihyaz.taskmanager.service.UserActivityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DemoApplicationTests {

	@InjectMocks
	TaskService taskService;

	@Mock
	TaskRepository taskRepository;

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	@InjectMocks
	UserActivityService userActivityService;

	@Mock
	UserActivityRepository userActivityRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createTaskTest_Success() {
		TaskDto taskDto = TaskDto.builder()
				.taskName("task1")
				.taskDescription("task1 description")
				.build();

		Task expectedTask = Task.builder()
				.taskName("task1")
				.taskDescription("task1 description")
				.build();

		Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(expectedTask);

		Task createdTask = taskService.createTask(taskDto);
		createdTask.setTaskId(null); // Ignore auto-generated IDs for comparison

		assertEquals(expectedTask, createdTask, "The task should have been created successfully");
	}

	@Test
	void createTaskTest_Failure_MissingName() {
		TaskDto taskDto = TaskDto.builder()
				.taskDescription("task1 description")
				.build();

		assertThrows(TaskExceptions.class, () -> taskService.createTask(taskDto), "Creating a task without a name should throw a TaskExceptions");
	}

	@Test
	void createTaskTest_Failure_MissingDescription() {
		TaskDto taskDto = TaskDto.builder()
				.taskName("task2")
				.build();

		assertThrows(TaskExceptions.class, () -> taskService.createTask(taskDto), "Creating a task without a description should throw a TaskExceptions");
	}

	@Test
	void createTaskTest_Failure_NullTask() {
		assertThrows(TaskExceptions.class, () -> taskService.createTask(null), "Creating a task with a null payload should throw a TaskExceptions");
	}

	@Test
	void getTaskByIdTest_Success() throws Exception {
		UUID taskId = UUID.fromString("86c019c7-3416-4ec7-9c3c-3fdc0ba067ea");
		UUID userId = UUID.fromString("9ddc7cfe-fee6-4b1c-9391-d2db5a2b954b");
		User user = User.builder().userId(userId).username("jack").build();
		Task expectedTask = Task.builder()
				.taskDescription("Make a todo app")
				.taskName("todo-App")
				.taskId(taskId)
				.assignedTo(user)
				.build();

		Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(expectedTask));
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		Task retrievedTask = taskService.getTaskById(taskId);
		retrievedTask.setLocalDateTime(null); // Ignore auto-generated timestamp for comparison

		assertEquals(expectedTask, retrievedTask, "The fetched task should match the expected task");
	}

	@Test
	void getTaskByIdTest_Failure() {
		assertThrows(TaskExceptions.class, () -> taskService.getTaskById(null), "Fetching a task with null UUID should throw TaskExceptions");
	}

	@Test
	void deleteTaskTest_Success() throws Exception {
		UUID taskId = UUID.randomUUID();
		Task task = Task.builder()
				.taskId(taskId)
				.taskName("Delete Task")
				.taskDescription("Description for delete task")
				.build();

		Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
		Mockito.doNothing().when(taskRepository).deleteById(taskId);

		taskService.deleteTask(taskId);

		Mockito.verify(taskRepository, Mockito.times(1)).deleteById(taskId);
		Mockito.verify(userActivityRepository, Mockito.times(1)).deleteAll(Mockito.anyList());
	}

	@Test
	void deleteTaskTest_Failure() {
		UUID taskId = UUID.randomUUID();
		Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

		assertThrows(TaskExceptions.class, () -> taskService.deleteTask(taskId), "Deleting a non-existing task should throw TaskExceptions");
	}

	@Test
	void listTasksTest_Success() {
		List<Task> expectedTasks = new ArrayList<>();
		expectedTasks.add(Task.builder().taskName("task1").taskDescription("Description 1").build());
		expectedTasks.add(Task.builder().taskName("task2").taskDescription("Description 2").build());
		Mockito.when(taskRepository.findAll()).thenReturn(expectedTasks);
		List<Task> tasks = taskService.listTasks();

		assertEquals(expectedTasks, tasks, "The listed tasks should match the expected tasks");
	}

	@Test
	void listTasksTest_Failure() {
		Mockito.when(taskRepository.findAll()).thenReturn(new ArrayList<>());

		assertThrows(TaskExceptions.class, () -> taskService.listTasks(), "Fetching an empty task list should throw TaskExceptions");
	}

	@Test
	void getAllAssignedTasksTest_Success() {
		List<Task> tasks = new ArrayList<>();
		tasks.add(Task.builder().taskName("task1").taskDescription("Description 1").assignedTo(new User()).build());
		Mockito.when(taskRepository.findAll()).thenReturn(tasks);

		List<Task> assignedTasks = taskService.getAllAssignedTasks();

		assertEquals(1, assignedTasks.size(), "There should be one assigned task");
	}

	@Test
	void getAllAssignedTasksTest_Failure() {
		List<Task> tasks = new ArrayList<>();
		tasks.add(Task.builder().taskName("task1").taskDescription("Description 1").build());
		Mockito.when(taskRepository.findAll()).thenReturn(tasks);

		assertThrows(TaskExceptions.class, () -> taskService.getAllAssignedTasks(), "Fetching an empty assigned task list should throw TaskExceptions");
	}

	@Test
	void getAllUnassignedTasksTest_Success() {
		List<Task> tasks = new ArrayList<>();
		tasks.add(Task.builder().taskName("task1").taskDescription("Description 1").build());
		Mockito.when(taskRepository.findAll()).thenReturn(tasks);

		List<Task> unassignedTasks = taskService.getAllUnassignedTasks();

		assertEquals(1, unassignedTasks.size(), "There should be one unassigned task");
	}

	@Test
	void getAllUnassignedTasksTest_Failure() {
		List<Task> tasks = new ArrayList<>();
		Mockito.when(taskRepository.findAll()).thenReturn(tasks);

		assertThrows(TaskExceptions.class, () -> taskService.getAllUnassignedTasks(), "Fetching an empty unassigned task list should throw TaskExceptions");
	}

	@Test
	void assignTaskTest_Success() {
		UUID taskId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		User user = User.builder().userId(userId).username("test_user").build();
		Task task = Task.builder().taskId(taskId).taskName("Test Task").taskDescription("Description").build();

		Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

		Task assignedTask = taskService.assignTask(taskId, userId);

		assertEquals(userId, assignedTask.getAssignedTo().getUserId(), "The task should be assigned to the given user");
	}

	@Test
	void reassignTaskTest_Success() throws Exception {
		UUID taskId = UUID.randomUUID();
		UUID oldUserId = UUID.randomUUID();
		UUID newUserId = UUID.randomUUID();
		User oldUser = User.builder().userId(oldUserId).username("old_user").build();
		User newUser = User.builder().userId(newUserId).username("new_user").build();
		Task task = Task.builder().taskId(taskId).taskName("Test Task").taskDescription("Description").assignedTo(oldUser).build();

		Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
		Mockito.when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
		Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

		Task reassignedTask = taskService.reassignTask(taskId, newUserId);

		assertEquals(newUserId, reassignedTask.getAssignedTo().getUserId(), "The task should be reassigned to the new user");
	}

	@Test
	void reassignTaskTest_Failure() throws Exception {
		UUID taskId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

		assertThrows(TaskExceptions.class, () -> taskService.reassignTask(taskId, userId), "Reassigning a non-existing task should throw TaskExceptions");
	}
}