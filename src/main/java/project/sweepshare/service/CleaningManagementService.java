package project.sweepshare.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.*;
import project.sweepshare.database.repository.*;
import project.sweepshare.enums.CleaningStyle;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CleaningManagementService {
    private final IWgsRepository wgsRepository;
    private final IUsersRepository usersRepository;
    private final IRoomsAssignmentsRepository roomAssignmentsRepository;
    private final ITasksRepository tasksRepository;
    private final ITasksAssignmentsRepository taskAssignmentsRepository;

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

        assignment.setIsCompleted(true);
        roomAssignmentsRepository.save(assignment);
    }

    @Transactional
    public void completeTaskAssignment(Long id,String email){
        TasksAssignmentsEntity assignment = taskAssignmentsRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Task assignment not found"));

        if(assignment.getUser() == null || !assignment.getUser().getEmail().equals(email)){
            throw new RuntimeException("You don't have permission to do this");
        }

        assignment.setIsCompleted(true);
        taskAssignmentsRepository.save(assignment);
    }

}
