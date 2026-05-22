package project.sweepshare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.TasksAssignmentsEntity;
import project.sweepshare.database.model.TasksEntity;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.repository.ITasksAssignmentsRepository;
import project.sweepshare.database.repository.ITasksRepository;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.dto.TaskAssignmentRequestDTO;
import project.sweepshare.dto.TaskAssignmentResponseDTO;
import project.sweepshare.mapper.ITaskAssignmentsMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService {

    private final ITasksAssignmentsRepository assignmentsRepository;
    private final ITasksRepository tasksRepository;
    private final IUsersRepository usersRepository;
    private final ITaskAssignmentsMapper assignmentsMapper;

    @Transactional
    public TaskAssignmentResponseDTO createAssignment(TaskAssignmentRequestDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TasksEntity task = tasksRepository.findById(dto.taskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        UsersEntity targetUser = usersRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userWgId = user.getWg().getId() != null ? user.getWg().getId() : null;
        Long taskWgId = task.getRoom().getWg().getId() != null ? task.getRoom().getWg().getId() : null;
        Long targetWgId =  targetUser.getWg().getId() != null ? targetUser.getWg().getId() : null;

        if(userWgId == null || !userWgId.equals(taskWgId) || !userWgId.equals(targetWgId)){
            throw new RuntimeException("Access denied");
        }

        if(assignmentsRepository.existsByTaskId(dto.taskId())) {
            throw new RuntimeException("Task already assigned");
        }

        TasksAssignmentsEntity assignment = TasksAssignmentsEntity.builder()
                .task(task)
                .user(targetUser)
                .build();

        TasksAssignmentsEntity savedAssignment = assignmentsRepository.save(assignment);
        return assignmentsMapper.toResponseDTO(savedAssignment);
    }

    @Transactional(readOnly = true)
    public List<TaskAssignmentResponseDTO> getMyWgTaskScale() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getWg().getId() == null){
            throw new RuntimeException("User does not belong to a WG");
        }

        List<TasksAssignmentsEntity> assignments = assignmentsRepository.findByTaskRoomWgId(user.getWg().getId());
        return assignmentsMapper.toResponseDTOList(assignments);
    }

    @Transactional
    public void removeAssignment(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TasksAssignmentsEntity assignment = assignmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if(user.getWg() == null){
            throw new RuntimeException("Access denied");
        }

        assignmentsRepository.delete(assignment);
    }

}
