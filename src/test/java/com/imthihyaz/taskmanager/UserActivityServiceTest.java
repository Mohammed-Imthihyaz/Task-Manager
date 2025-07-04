package com.imthihyaz.taskmanager;

import com.imthihyaz.taskmanager.dao.UserActivityRepository;
import com.imthihyaz.taskmanager.dto.UserActivityDto;
import com.imthihyaz.taskmanager.exceptions.UserActivityException;
import com.imthihyaz.taskmanager.model.UsersActivity;
import com.imthihyaz.taskmanager.model.Task;
import com.imthihyaz.taskmanager.model.User;
import com.imthihyaz.taskmanager.service.UserActivityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserActivityServiceTest {

    @InjectMocks
    UserActivityService userActivityService;

    @Mock
    UserActivityRepository userActivityRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUserActivityTaskTest_Success() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        User user = User.builder().userId(userId).username("testUser").build();
        Task task = Task.builder().taskId(taskId).taskName("testTask").build();
        UserActivityDto userActivityDto = UserActivityDto.builder()
                .user(user)
                .task(task)
                .startTime(LocalDateTime.now())
                .build();
        UsersActivity usersActivity = UsersActivity.builder()
                .user(user)
                .task(task)
                .isActive(true)
                .startTime(LocalDateTime.now())
                .build();

        Mockito.when(userActivityRepository.findByUser_UserIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());
        Mockito.when(userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId)).thenReturn(Optional.empty());
        Mockito.when(userActivityRepository.save(Mockito.any(UsersActivity.class))).thenReturn(usersActivity);

        userActivityService.addUserActivityTask(userActivityDto);

        Mockito.verify(userActivityRepository, Mockito.times(1)).save(Mockito.any(UsersActivity.class));
    }

    @Test
    void addUserActivityTaskTest_Failure_NullDto() {
        assertThrows(UserActivityException.class, () -> {
            userActivityService.addUserActivityTask(null);
        }, "Adding a null UserActivityDto should throw UserActivityException");
    }

    @Test
    void addUserActivityTaskTest_Failure_NullUser() {
        UserActivityDto userActivityDto = UserActivityDto.builder()
                .task(Task.builder().taskId(UUID.randomUUID()).taskName("testTask").build())
                .startTime(LocalDateTime.now())
                .build();

        assertThrows(UserActivityException.class, () -> {
            userActivityService.addUserActivityTask(userActivityDto);
        }, "Adding a UserActivityDto with null User should throw UserActivityException");
    }

    @Test
    void addUserActivityTaskTest_Failure_NullTask() {
        UserActivityDto userActivityDto = UserActivityDto.builder()
                .user(User.builder().userId(UUID.randomUUID()).username("testUser").build())
                .startTime(LocalDateTime.now())
                .build();

        assertThrows(UserActivityException.class, () -> {
            userActivityService.addUserActivityTask(userActivityDto);
        }, "Adding a UserActivityDto with null Task should throw UserActivityException");
    }

    @Test
    void saveOrUpdateUserActivityTest_Success() {
        UsersActivity usersActivity = UsersActivity.builder()
                .id(UUID.randomUUID())
                .user(User.builder().userId(UUID.randomUUID()).username("testUser").build())
                .task(Task.builder().taskId(UUID.randomUUID()).taskName("testTask").build())
                .isActive(true)
                .startTime(LocalDateTime.now())
                .build();

        Mockito.when(userActivityRepository.save(Mockito.any(UsersActivity.class))).thenReturn(usersActivity);

        userActivityService.saveOrUpdateUserActivity(usersActivity);

        Mockito.verify(userActivityRepository, Mockito.times(1)).save(Mockito.any(UsersActivity.class));
    }

    @Test
    void makeUserInActiveTest_Success() {
        UUID taskId = UUID.randomUUID();
        UsersActivity usersActivity = UsersActivity.builder()
                .task(Task.builder().taskId(taskId).taskName("testTask").build())
                .user(User.builder().userId(UUID.randomUUID()).username("testUser").build())
                .isActive(true)
                .startTime(LocalDateTime.now())
                .build();

        Mockito.when(userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId)).thenReturn(Optional.of(usersActivity));

        userActivityService.makeUserInActive(taskId);

        Mockito.verify(userActivityRepository, Mockito.times(1)).save(Mockito.any(UsersActivity.class));
        assertEquals(false, usersActivity.isActive());
    }

    @Test
    void makeUserInActiveTest_Failure() {
        UUID taskId = UUID.randomUUID();

        Mockito.when(userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId)).thenReturn(Optional.empty());

        assertThrows(UserActivityException.class, () -> {
            userActivityService.makeUserInActive(taskId);
        }, "Making an inactive user task with non-existing taskId should throw UserActivityException");
    }

    @Test
    void getAllActiveTasksTest_Success() {
        List<UsersActivity> expectedActiveTasks = new ArrayList<>();
        expectedActiveTasks.add(UsersActivity.builder()
                .user(User.builder().userId(UUID.randomUUID()).username("testUser").build())
                .task(Task.builder().taskId(UUID.randomUUID()).taskName("testTask").build())
                .isActive(true)
                .startTime(LocalDateTime.now())
                .build());

        Mockito.when(userActivityRepository.findByIsActiveTrue()).thenReturn(expectedActiveTasks);

        List<UsersActivity> activeTasks = userActivityService.getAllActiveTasks();

        assertEquals(expectedActiveTasks, activeTasks);
    }

    @Test
    void getAllActiveTasksTest_Failure() {
        Mockito.when(userActivityRepository.findByIsActiveTrue()).thenReturn(new ArrayList<>());

        assertThrows(UserActivityException.class, () -> {
            userActivityService.getAllActiveTasks();
        }, "Fetching all active tasks when none are present should throw UserActivityException");
    }

    @Test
    void getActiveTaskByIdTest_Success() {
        UUID taskId = UUID.randomUUID();
        UsersActivity expectedUserActivity = UsersActivity.builder()
                .task(Task.builder().taskId(taskId).taskName("testTask").build())
                .user(User.builder().userId(UUID.randomUUID()).username("testUser").build())
                .isActive(true)
                .startTime(LocalDateTime.now())
                .build();

        Mockito.when(userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId)).thenReturn(Optional.of(expectedUserActivity));

        Optional<UsersActivity> actualUserActivity = userActivityService.getActiveTaskById(taskId);

        assertEquals(expectedUserActivity, actualUserActivity.get());
    }

    @Test
    void getActiveTaskByIdTest_Failure() {
        UUID taskId = UUID.randomUUID();

        Mockito.when(userActivityRepository.findByTask_TaskIdAndIsActiveTrue(taskId)).thenReturn(Optional.empty());

        assertThrows(UserActivityException.class, () -> {
            userActivityService.getActiveTaskById(taskId).orElseThrow(() -> new UserActivityException("No active task found"));
        });
    }

    @Test
    void getActiveUserByIdTest_Success() {
        UUID userId = UUID.randomUUID();
        UsersActivity expectedUserActivity = UsersActivity.builder()
                .user(User.builder().userId(userId).username("testUser").build())
                .task(Task.builder().taskId(UUID.randomUUID()).taskName("testTask").build())
                .isActive(true)
                .startTime(LocalDateTime.now())
                .build();

        Mockito.when(userActivityRepository.findByUser_UserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(expectedUserActivity));

        Optional<UsersActivity> actualUserActivity = userActivityService.getActiveUserById(userId);

        assertEquals(expectedUserActivity, actualUserActivity.get());
    }

    @Test
    void getActiveUserByIdTest_Failure() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userActivityRepository.findByUser_UserIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        assertThrows(UserActivityException.class, () -> {
            userActivityService.getActiveUserById(userId).orElseThrow(() -> new UserActivityException("No active user found"));
        });
    }

    @Test
    void allInActiveUsersOfTaskTest_Success() {
        UUID taskId = UUID.randomUUID();
        List<UsersActivity> expectedInactiveUsers = new ArrayList<>();
        expectedInactiveUsers.add(UsersActivity.builder()
                .task(Task.builder().taskId(taskId).taskName("testTask").build())
                .user(User.builder().userId(UUID.randomUUID()).username("testUser").build())
                .isActive(false)
                .startTime(LocalDateTime.now())
                .build());

        Mockito.when(userActivityRepository.findByTask_TaskIdAndIsActiveFalse(taskId)).thenReturn(expectedInactiveUsers);

        List<UsersActivity> inactiveUsers = userActivityService.allInActiveUsersOfTask(taskId);

        assertEquals(expectedInactiveUsers, inactiveUsers);
    }

    @Test
    void allInActiveUsersOfTaskTest_Failure() {
        UUID taskId = UUID.randomUUID();

        Mockito.when(userActivityRepository.findByTask_TaskIdAndIsActiveFalse(taskId)).thenReturn(new ArrayList<>());

        assertThrows(UserActivityException.class, () -> {
            userActivityService.allInActiveUsersOfTask(taskId);
        }, "Fetching all inactive users for a task when none are present should throw UserActivityException");
    }
}