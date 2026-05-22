package project.sweepshare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.RoomsEntity;
import project.sweepshare.database.model.TasksEntity;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.repository.IRoomsRepository;
import project.sweepshare.database.repository.ITasksRepository;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.dto.TasksRequestDTO;
import project.sweepshare.dto.TasksResponseDTO;
import project.sweepshare.mapper.ITasksMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TasksService {

    private final ITasksRepository tasksRepository;
    private final IUsersRepository usersRepository;
    private final IRoomsRepository roomsRepository;
    private final ITasksMapper tasksMapper;

    @Transactional
    public TasksResponseDTO createTask(TasksRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        RoomsEntity room = roomsRepository.findById(dto.roomId())
                .orElseThrow(()-> new RuntimeException("Room not found"));

        if (user.getWg() == null || !room.getWg().getId().equals(user.getWg().getId())) {
            throw new RuntimeException("User not authorized to perform this action");
        }

        if (tasksRepository.existsByNameIgnoreCaseAndRoomId(dto.name(), dto.roomId())) {
            throw new RuntimeException("Task already exists in this room");
        }

        TasksEntity taskEntity = tasksMapper.toEntity(dto);
        taskEntity.setRoom(room);

        TasksEntity savedTask = tasksRepository.save(taskEntity);
        return tasksMapper.toResponseDTO(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TasksResponseDTO> findByRoom(Long roomId) {
        List<TasksEntity> tasks = tasksRepository.findByRoomId(roomId);
        return tasksMapper.toResponseDTOList(tasks);
    }

    @Transactional
    public TasksResponseDTO updateTask(TasksRequestDTO dto, Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        RoomsEntity room = roomsRepository.findById(dto.roomId())
                .orElseThrow(()-> new RuntimeException("Room not found"));

        TasksEntity task = tasksRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Task not found"));

        if (user.getWg() == null || !room.getWg().getId().equals(user.getWg().getId())) {
            throw new RuntimeException("User not authorized to perform this action");
        }

        if(dto.name() != null && !dto.name().isBlank()) {
            if (!task.getName().equalsIgnoreCase(dto.name()) &&
                    tasksRepository.existsByNameIgnoreCaseAndRoomId(dto.name(), task.getRoom().getId())) {
                throw new RuntimeException("Task already exists in this room");
            }
            task.setName(dto.name());
        }

        if(dto.level() != null){
            task.setLevel(dto.level());
        }

        TasksEntity savedTask = tasksRepository.save(task);
        return tasksMapper.toResponseDTO(savedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        TasksEntity task = tasksRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Task not found"));

        if(user.getWg() == null || !task.getRoom().getWg().getId().equals(user.getWg().getId())) {
            throw new RuntimeException("User not authorized to perform this action");
        }

        tasksRepository.delete(task);
    }
}
