package project.sweepshare.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.dto.UsersRequestDTO;
import project.sweepshare.dto.UsersResponseDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IUsersMapper {
    UsersResponseDTO toResponseDTO(UsersEntity user);

    UsersEntity toEntity(UsersRequestDTO requestDTO);

    void updateEntityFromDto(UsersRequestDTO requestDTO, @MappingTarget UsersEntity user);
}