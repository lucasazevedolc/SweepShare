package project.sweepshare.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.*;
import project.sweepshare.database.repository.*;
import project.sweepshare.dto.RoomOverviewDTO;
import project.sweepshare.dto.TaskStatusDTO;
import project.sweepshare.dto.WgCleaningStatusResponseDTO;
import project.sweepshare.enums.CleaningStyle;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CleaningManagementService {
    private final IWgsRepository wgsRepository;
    private final IUsersRepository usersRepository;
    private final IRoomsAssignmentsRepository roomAssignmentsRepository;
    private final ITasksRepository tasksRepository;
    private final ITasksAssignmentsRepository taskAssignmentsRepository;
    private final ICleaningHistoryRepository cleaningHistoryRepository;
    private final IRoomsRepository roomsRepository;

    @Transactional
    public void executeAllCleaningStrategies() {
        List<WgsEntity> rotationWgs = wgsRepository.findByCleaningStyle(CleaningStyle.WEEKLY_ROTATION.ordinal());

        for (WgsEntity wg : rotationWgs) {
            try {
                rotateSingleWg(wg);
            } catch (Exception e) {
                System.err.println(("Failed to rotate room for WgId: " + wg.getId()));
            }
        }

        List<WgsEntity> taskDistributionWgs = wgsRepository.findByCleaningStyle(CleaningStyle.TASK_AMOUNT.ordinal());

        for (WgsEntity wg : taskDistributionWgs) {
            try {
                distributeTasksForSingleWg(wg);
            }catch (Exception e){
                System.err.println(("Failed to distribute tasks for WgId: " + wg.getId()));
            }
        }
    }

    public void rotateSingleWg(WgsEntity wg){
        List<UsersEntity> members = usersRepository.findByWgIdAndActiveTrueOrderByNameAsc(wg.getId());

        if(members.isEmpty()){
            System.err.println("Failed to fetch Wg Members, WgId: " + wg.getId());
        }

        List<RoomsAssignmentsEntity> assignments = roomAssignmentsRepository.findByRoomWgId(wg.getId());
        if(assignments.isEmpty()){
            System.err.println("WG has no active room assignment to rotate, WgId: " + wg.getId());
        }

        int previousUserIndex = 0;
        if(assignments.get(0).getUser() != null){
            previousUserIndex = members.indexOf(assignments.get(0).getUser());
        }

        int startingIndex = (previousUserIndex + 1) % assignments.size();
        if(assignments.size() % members.size() != 0){
            startingIndex = (startingIndex + (assignments.size() % members.size()) -1) % members.size();
        }

        for(int i=0; i<assignments.size(); i++){
            RoomsAssignmentsEntity assignment = assignments.get(i);

            int memberIndex = (startingIndex + i) % members.size();
            assignment.setUser(members.get(memberIndex));
            assignment.setIsCompleted(false);
        }

        roomAssignmentsRepository.saveAll(assignments);
    }

    @Transactional
    public void forceRotation(Long wgId, String userEmail){
        UsersEntity  user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getWg().getCleaningStyle() == CleaningStyle.FIXED_PER_ROOM.ordinal()){
            throw new RuntimeException("There's no rotation for this WG");
        }

        if(user.getWg() == null){
            throw new RuntimeException("You do not belong to a WG");
        }

        if(!user.getWg().getId().equals(wgId)){
            throw new RuntimeException("You don't have permission to do this");
        }

        WgsEntity wg = wgsRepository.findById(wgId)
                .orElseThrow(() -> new RuntimeException("WG not found"));

        if(wg.getCleaningStyle() == CleaningStyle.WEEKLY_ROTATION.ordinal()){
            rotateSingleWg(wg);
        }

        if(wg.getCleaningStyle() == CleaningStyle.TASK_AMOUNT.ordinal()){
            distributeTasksForSingleWg(wg);
        }


    }

    public void distributeTasksForSingleWg(WgsEntity wg){
        List<UsersEntity> members = usersRepository.findByWgIdAndActiveTrueOrderByNameAsc(wg.getId());
        if(members.isEmpty()){
            System.err.println("Failed to fetch Wg Members, WgId: " + wg.getId());
        }
        java.util.Collections.shuffle(members);

        List<TasksEntity> tasks = tasksRepository.findByRoomWgId(wg.getId());
        if(tasks.isEmpty()){
            System.err.println("No tasks found to distribute for WgId: " + wg.getId());
        }

        tasks.sort(Comparator.comparingInt(TasksEntity::getLevel).reversed());

        Map<UsersEntity, Integer> userWorkloadMap = new HashMap<>();
        for(UsersEntity member : members){
            userWorkloadMap.put(member, 0);
        }

        List<TasksAssignmentsEntity> updatedAssignments= new ArrayList<>();

        for(TasksEntity task : tasks){
            UsersEntity leastBurdenedUser = userWorkloadMap.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow(()-> new RuntimeException("Failed to evaluate member workload"))
                    .getKey();

            TasksAssignmentsEntity assignment = taskAssignmentsRepository.findByTaskId(task.getId())
                    .orElseGet(() -> TasksAssignmentsEntity.builder()
                            .task(task)
                            .build());

            assignment.setUser(leastBurdenedUser);
            assignment.setIsCompleted(false);
            updatedAssignments.add(assignment);

            int currentLoad = userWorkloadMap.get(leastBurdenedUser);
            userWorkloadMap.put(leastBurdenedUser, currentLoad + task.getLevel());
        }

        taskAssignmentsRepository.saveAll(updatedAssignments);
    }

    @Transactional
    public void completeRoomAssignment(Long id,String email){
        RoomsAssignmentsEntity assignment = roomAssignmentsRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Room assignment not found"));

        if(assignment.getUser() == null || !assignment.getUser().getEmail().equals(email)){
            throw new RuntimeException("You don't have permission to do this");
        }

        if(!assignment.getIsCompleted()){
            assignment.setIsCompleted(true);
            roomAssignmentsRepository.save(assignment);

            CleaningHistoryEntity history = CleaningHistoryEntity.builder()
                    .wg(assignment.getRoom().getWg())
                    .user(assignment.getUser())
                    .room(assignment.getRoom())
                    .task(null)
                    .cleanedAt(LocalDateTime.now())
                    .wasCompleted(true)
                    .build();

            cleaningHistoryRepository.save(history);
        }
    }

    @Transactional
    public void completeTaskAssignment(Long id,String email){
        TasksAssignmentsEntity assignment = taskAssignmentsRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Task assignment not found"));

        if(assignment.getUser() == null || !assignment.getUser().getEmail().equals(email)){
            throw new RuntimeException("You don't have permission to do this");
        }

        if(!assignment.getIsCompleted()) {
            assignment.setIsCompleted(true);
            taskAssignmentsRepository.save(assignment);

            CleaningHistoryEntity history = CleaningHistoryEntity.builder()
                    .wg(assignment.getTask().getRoom().getWg())
                    .user(assignment.getUser())
                    .room(assignment.getTask().getRoom())
                    .task(assignment.getTask())
                    .cleanedAt(LocalDateTime.now())
                    .wasCompleted(true)
                    .build();

            cleaningHistoryRepository.save(history);
        }
    }

    @Transactional(readOnly = true)
    public WgCleaningStatusResponseDTO getWgCleaningStatus(Long wgId){
        WgsEntity wg = wgsRepository.findById(wgId)
                .orElseThrow(()-> new RuntimeException("Wg not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        if(user.getWg() == null){
            throw new RuntimeException("User doesn't belong to a WG");
        }

        if(!(user.getWg().getId()).equals(wg.getId())){
            throw new RuntimeException("You don't have  permission to do this");
        }

        List<RoomsEntity> rooms = roomsRepository.findByWgId(wg.getId());

        List<CleaningHistoryEntity> historyList = cleaningHistoryRepository.findByWgId(wg.getId());

        List<RoomOverviewDTO> roomsOverview = new ArrayList<>();

        for(RoomsEntity room : rooms){
            CleaningHistoryEntity lastSuccessClean = historyList.stream()
                    .filter(h -> h.getRoom().getId().equals(room.getId())
                            && Boolean.TRUE.equals(h.getWasCompleted()))
                    .max(Comparator.comparing(CleaningHistoryEntity::getCleanedAt))
                    .orElse(null);

            LocalDateTime lastTime = lastSuccessClean != null ? lastSuccessClean.getCleanedAt() : null;
            String lastUser = lastSuccessClean != null ? lastSuccessClean.getUser().getName() : "Never cleaned";

            String currentResponsible = "No one assigned";
            Boolean isRoomDone = false;
            List<TaskStatusDTO> taskStatusList = new ArrayList<>();

            if(wg.getCleaningStyle() == CleaningStyle.WEEKLY_ROTATION.ordinal()
                    || wg.getCleaningStyle() == CleaningStyle.FIXED_PER_ROOM.ordinal()){

                RoomsAssignmentsEntity assignment = roomAssignmentsRepository.findByRoomWgId(wg.getId()).stream()
                        .filter(a -> a.getRoom().getId().equals(room.getId()))
                        .findFirst()
                        .orElse(null);

                if(assignment != null && assignment.getUser() != null){
                    currentResponsible = assignment.getUser().getName();
                    isRoomDone = assignment.getIsCompleted();
                }
            }

            else{
                List<TasksEntity> roomTasks = tasksRepository.findTasksWithRoomsByWgId(wg.getId()).stream()
                        .filter(t -> t.getRoom() != null && t.getRoom().getId().equals(room.getId()))
                        .toList();

                boolean allTasksDone = !roomTasks.isEmpty();
                Set<String> uniqueNames = new HashSet<>();

                for(TasksEntity task : roomTasks){
                    TasksAssignmentsEntity assignment = taskAssignmentsRepository.findByTaskId(task.getId())
                            .orElse(null);

                    Boolean taskDone = false;
                    if(assignment != null && assignment.getUser() != null){
                        uniqueNames.add(assignment.getUser().getName());
                        taskDone = assignment.getIsCompleted();
                    }

                    if(!Boolean.TRUE.equals(taskDone)){
                        allTasksDone = false;
                    }

                    taskStatusList.add(new TaskStatusDTO(
                            task.getId(),
                            task.getName(),
                            task.getLevel(),
                            taskDone
                    ));

                    currentResponsible = String.join(", ", uniqueNames);
                    if(currentResponsible.isEmpty()) currentResponsible = "No one assigned";
                    isRoomDone = allTasksDone;

                }
            }

            roomsOverview.add(new RoomOverviewDTO(
                    room.getId(),
                    room.getName(),
                    currentResponsible,
                    isRoomDone,
                    lastTime,
                    lastUser,
                    taskStatusList
            ));
        }

        String cleaningStyle = "UNKNOWN";
        if (wg.getCleaningStyle() == CleaningStyle.FIXED_PER_ROOM.ordinal()) {
            cleaningStyle = "FIXED_MEMBER";
        } else if (wg.getCleaningStyle() == CleaningStyle.WEEKLY_ROTATION.ordinal()) {
            cleaningStyle = "WEEKLY_ROTATION";
        } else if (wg.getCleaningStyle() == CleaningStyle.TASK_AMOUNT.ordinal()) {
            cleaningStyle = "TASK_AMOUNT";
        }

        return new WgCleaningStatusResponseDTO(
                wg.getId(),
                cleaningStyle,
                roomsOverview
        );
    }

}
