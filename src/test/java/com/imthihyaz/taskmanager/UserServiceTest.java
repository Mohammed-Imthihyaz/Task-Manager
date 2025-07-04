package com.imthihyaz.taskmanager;

import com.imthihyaz.taskmanager.dao.TaskRepository;
import com.imthihyaz.taskmanager.dao.UserRepository;
import com.imthihyaz.taskmanager.dto.UserDto;
import com.imthihyaz.taskmanager.exceptions.UsersException;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import com.imthihyaz.taskmanager.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    TaskRepository taskRepository;

    @Test
    void createUserTest_Success() {
        UserDto userDto = UserDto.builder()
                .userId(UUID.randomUUID())
                .name("Test User")
                .build();

        User expectedUser = User.builder()
                .userId(userDto.getUserId())
                .username(userDto.getName())
                .build();

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

        User createdUser = userService.createUser(userDto);

        assertEquals(expectedUser, createdUser);
    }

    @Test
    void listUsersTest_Success() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(User.builder().userId(UUID.randomUUID()).username("User1").build());
        expectedUsers.add(User.builder().userId(UUID.randomUUID()).username("User2").build());

        Mockito.when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> users = userService.listUsers();

        assertEquals(expectedUsers, users);
    }

    @Test
    void getUserByIdTest_Success() {
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder().userId(userId).username("Test User").build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(userId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserByIdTest_Failure() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UsersException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    void getUserTaskTest_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder().userId(userId).username("Test User").build();
        Task task1 = Task.builder().taskName("Task1").taskDescription("Description1").assignedTo(user).build();
        Task task2 = Task.builder().taskName("Task2").taskDescription("Description2").assignedTo(user).build();
        List<Task> allTasks = new ArrayList<>();
        allTasks.add(task1);
        allTasks.add(task2);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findAll()).thenReturn(allTasks);

        List<Task> userTasks = userService.getUserTask(userId);

        assertEquals(2, userTasks.size());
        assertEquals(task1.getTaskName(), userTasks.get(0).getTaskName());
        assertEquals(task2.getTaskName(), userTasks.get(1).getTaskName());
    }

    @Test
    void getUserTaskTest_Failure_UserNotFound() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UsersException.class, () -> {
            userService.getUserTask(userId);
        });
    }

    @Test
    void getUserTaskTest_Failure_NoTasksAssigned() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder().userId(userId).username("Test User").build();
        List<Task> allTasks = new ArrayList<>();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findAll()).thenReturn(allTasks);

        assertThrows(UsersException.class, () -> {
            userService.getUserTask(userId);
        });
    }

    @Test
    void deleteUserTest_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder().userId(userId).username("Test User").build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void deleteUserTest_Failure() throws Exception {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UsersException.class, () -> {
            userService.deleteUser(userId);
        });
    }
}