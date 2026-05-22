package project.sweepshare.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.sweepshare.database.model.RoomsAssignmentsEntity;
import project.sweepshare.dto.RoomsAssignmentRequestDTO;
import project.sweepshare.dto.RoomsAssignmentResponseDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IRoomsAssignmentsMapper {
    @Mapping(target = "room.id", source ="roomId")
    @Mapping(target = "user.id", source ="userId")
    RoomsAssignmentsEntity toEntity(RoomsAssignmentRequestDTO requestDTO);

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    RoomsAssignmentResponseDTO toResponseDTO(RoomsAssignmentsEntity assignment);

    List<RoomsAssignmentResponseDTO> toResponseDTOList(List<RoomsAssignmentsEntity> assignments);
}
