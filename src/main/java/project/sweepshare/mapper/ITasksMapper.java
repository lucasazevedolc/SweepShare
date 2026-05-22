package project.sweepshare.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.sweepshare.database.model.TasksEntity;
import project.sweepshare.dto.TasksRequestDTO;
import project.sweepshare.dto.TasksResponseDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITasksMapper {

    @Mapping(target = "room.id",source ="roomId")
    @Mapping(target = "level", source = "level")
    TasksEntity toEntity(TasksRequestDTO requestDto);

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName",source = "room.name")
    @Mapping(target = "level", source = "level")
    TasksResponseDTO toResponseDTO(TasksEntity task);

    List<TasksResponseDTO> toResponseDTOList(List<TasksEntity> tasks);
}
