package project.sweepshare.mapper;

import org.mapstruct.Mapper;
import project.sweepshare.database.model.RoomsEntity;
import project.sweepshare.dto.RoomsRequestDTO;
import project.sweepshare.dto.RoomsResponseDTO;

import java.util.List;


@Mapper(componentModel = "spring")
public interface IRoomsMapper {
    RoomsEntity toEntity(RoomsRequestDTO requestDTO);

    RoomsResponseDTO toResponseDTO(RoomsEntity room);

    List<RoomsResponseDTO> toResponseDTO(List<RoomsEntity> rooms);

}
