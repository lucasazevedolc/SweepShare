package project.sweepshare.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import project.sweepshare.database.model.*;
import project.sweepshare.database.repository.*;
import project.sweepshare.exception.AccessDeniedException;
import project.sweepshare.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CleaningManagementServiceTest {

    @Mock
    private IWgsRepository wgsRepository;
    @Mock
    private IUsersRepository usersRepository;
    @Mock
    private IRoomsAssignmentsRepository roomsAssignmentsRepository;
    @Mock
    private ITasksRepository tasksRepository;
    @Mock
    private ITasksAssignmentsRepository tasksAssignmentsRepository;
    @Mock
    private ICleaningHistoryRepository cleaningHistoryRepository;
    @Mock
    private IRoomsRepository roomsRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CleaningManagementService cleaningManagementService;

    private UsersEntity thiagoUser;
    private WgsEntity testWg;

    @BeforeEach
    void setUp() {
        testWg = new WgsEntity();
        testWg.setId(1L);
        testWg.setCleaningStyle(1); // Weekly Rotation

        thiagoUser = new UsersEntity();
        thiagoUser.setId(10L);
        thiagoUser.setName("Thiago");
        thiagoUser.setEmail("thiago@gmail.com");
        thiagoUser.setWg(testWg);

    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user doesn't belong to a WG")
    void forceRotation_ShouldThrowException_WhenUserHasNoWg() {
        String userEmail = "thiago@gmail.com";

        UsersEntity userWithoutWg = new UsersEntity();
        userWithoutWg.setId(10L);
        userWithoutWg.setEmail("userEmail");
        userWithoutWg.setWg(null);

        Mockito.when(usersRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(userWithoutWg));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->{
            cleaningManagementService.forceRotation(1L, userEmail);
        });

        assertEquals("You do not belong to a WG", exception.getMessage());

        Mockito.verifyNoInteractions(wgsRepository, roomsAssignmentsRepository, tasksAssignmentsRepository);

    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when WG cleaning style is FIXED_PER_ROOM")
    void forceRotation_ShouldThrowException_WhenWgCleaningStyleIsFixedPerRoom() {
        String userEmail = "thiago@gmail.com";

        WgsEntity fixedWg = new WgsEntity();
        fixedWg.setId(1L);
        fixedWg.setCleaningStyle(0); //FIXED_PER_ROOM

        UsersEntity userWithFixedWG = new UsersEntity();
        userWithFixedWG.setId(10L);
        userWithFixedWG.setEmail(userEmail);
        userWithFixedWG.setWg(fixedWg);

        Mockito.when(usersRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(userWithFixedWG));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class , () ->{
            cleaningManagementService.forceRotation(1L, userEmail);
        });

        assertEquals("There's no rotation for this WG", exception.getMessage());
        Mockito.verifyNoInteractions(wgsRepository, roomsAssignmentsRepository, tasksAssignmentsRepository);

    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user tries to force rotation of another WG")
    void forceRotation_ShouldThrowException_WhenUserDoesNotBelongToTargetWg() {
        String userEmail = "thiago@gmail.com";
        Long targetWgId = 2L;

        Mockito.when(usersRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(thiagoUser));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->{
            cleaningManagementService.forceRotation(targetWgId, userEmail);
        });

        assertEquals("You don't have permission to do this", exception.getMessage());
        Mockito.verifyNoInteractions(wgsRepository, roomsAssignmentsRepository, tasksAssignmentsRepository);

    }

    @Test
    @DisplayName("Should successfully rotate rooms among WG members and reset completed status")
    void rotateSingleWg_ShouldSuccess_WhenValidWgAndAssignmentsExist() {
        String thiagoEmail = "thiago@gmail.com";
        thiagoUser.setEmail(thiagoEmail);

        UsersEntity marvinUser = new UsersEntity();
        marvinUser.setId(11L);
        marvinUser.setName("Marvin");
        marvinUser.setEmail("marvin@gmail.com");
        marvinUser.setWg(testWg);

        List<UsersEntity> mockMembers = List.of(marvinUser, thiagoUser);
        testWg.setMembers(mockMembers);

        RoomsEntity kitchen =  new RoomsEntity();
        kitchen.setId(101L);
        kitchen.setName("Kitchen");
        kitchen.setWg(testWg);

        RoomsEntity bathroom =  new RoomsEntity();
        bathroom.setId(102L);
        bathroom.setName("Bathroom");
        bathroom.setWg(testWg);

        RoomsAssignmentsEntity assignment1 = new RoomsAssignmentsEntity();
        assignment1.setId(501L);
        assignment1.setRoom(kitchen);
        assignment1.setUser(marvinUser);
        assignment1.setIsCompleted(true);

        RoomsAssignmentsEntity assignment2 = new RoomsAssignmentsEntity();
        assignment2.setId(502L);
        assignment2.setRoom(bathroom);
        assignment2.setUser(thiagoUser);
        assignment2.setIsCompleted(true);

        List<RoomsAssignmentsEntity> mockAssignments = List.of(assignment1, assignment2);

        Mockito.when(usersRepository.findByWgIdAndActiveTrueOrderByNameAsc(testWg.getId()))
                .thenReturn(mockMembers);

        Mockito.when(roomsAssignmentsRepository.findByRoomWgId(testWg.getId()))
                .thenReturn(mockAssignments);

        cleaningManagementService.rotateSingleWg(testWg);

        assertFalse(assignment1.getIsCompleted(),"Kitchen assignment should be reset to incomplete");
        assertFalse(assignment2.getIsCompleted(),"Bathroom assignment should be reset to incomplete");

        assertEquals(thiagoUser, assignment1.getUser(), "Kitchen should now be assigned to Thiago");
        assertEquals(marvinUser, assignment2.getUser(), "Bathroom should now be assigned to Thiago");

        Mockito.verify(roomsAssignmentsRepository, Mockito.times(1)).saveAll(mockAssignments);

        Mockito.verify(notificationService, Mockito.times(2))
                .sendNewRoomScheduleEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyList());

    }

    @Test
    @DisplayName("Should distribute tasks fairly using greedy workload algorithm and send consolidated email")
    void distributeTasksForSingleWg_ShouldSuccess_WhenValidWgAndTasksExist() {
        String thiagoEmail = "thiago@gmail.com";
        thiagoUser.setEmail(thiagoEmail);

        UsersEntity marvinUser = new UsersEntity();
        marvinUser.setId(11L);
        marvinUser.setName("Marvin");
        marvinUser.setEmail("marvin@gmail.com");
        marvinUser.setWg(testWg);

        List<UsersEntity> mockMembers = new ArrayList<>(List.of(marvinUser, thiagoUser));
        testWg.setMembers(mockMembers);

        TasksEntity heavyTask = new TasksEntity();
        heavyTask.setId(201L);
        heavyTask.setName("Clean Bathroom");
        heavyTask.setLevel(3);

        TasksEntity mediumTask = new TasksEntity();
        mediumTask.setId(202L);
        mediumTask.setName("Clean Kitchen");
        mediumTask.setLevel(2);

        TasksEntity lightTask = new TasksEntity();
        lightTask.setId(203L);
        lightTask.setName("Vacuum Hallway");
        lightTask.setLevel(1);

        List<TasksEntity> mockTasks = new ArrayList<>(List.of(heavyTask,mediumTask,lightTask));

        Mockito.when(usersRepository.findByWgIdAndActiveTrueOrderByNameAsc(testWg.getId()))
                .thenReturn(mockMembers);

        Mockito.when(tasksRepository.findByRoomWgId(testWg.getId()))
                .thenReturn(mockTasks);

        Mockito.when(tasksAssignmentsRepository.findByTaskId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        cleaningManagementService.distributeTasksForSingleWg(testWg);

        Mockito.verify(tasksAssignmentsRepository, Mockito.times(1)).saveAll(Mockito.anyList());

        Mockito.verify(notificationService, Mockito.times(2))
                .sendNewTaskScheduleEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyList());

    }

}
