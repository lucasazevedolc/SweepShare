package project.sweepshare.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.sweepshare.database.model.TasksAssignmentsEntity;
import project.sweepshare.dto.TaskAssignmentRequestDTO;
import project.sweepshare.dto.TaskAssignmentResponseDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITaskAssignmentsMapper {
    @Mapping(target = "task.id",source = "taskId")
    @Mapping(target = "user.id",source = "userId")
    TasksAssignmentsEntity toEntity(TaskAssignmentRequestDTO requestDTO);

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "taskName", source = "task.name")
    @Mapping(target = "taskLevel", source = "task.level")
    @Mapping(target = "roomId", source = "task.room.id")
    @Mapping(target = "roomName", source = "task.room.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    TaskAssignmentResponseDTO toResponseDTO(TasksAssignmentsEntity assignment);

    List<TaskAssignmentResponseDTO> toResponseDTOList(List<TasksAssignmentsEntity> assignments);
}
