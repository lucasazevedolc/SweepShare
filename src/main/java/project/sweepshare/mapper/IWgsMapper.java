package project.sweepshare.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;
import project.sweepshare.dto.WgsRequestDTO;
import project.sweepshare.dto.WgsResponseDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IWgsMapper {

    @Named("intToCleaningStyle")
    default String mapCleaningStyle(int style){
        return switch(style){
            case 0 -> "Person fixed per room";
            case 1 -> "Weekly rotation";
            case 2 -> "Weekly task amount";
            default -> "Unknown";
        };
    }

    @Named("intToRentStyle")
    default String mapRentStyle(int style){
        return switch (style){
            case 0 -> "Each person has their own contract";
            case 1 -> "Single head lease";
            default -> "Unknown";
        };
    }

    @Mapping(target = "cleaningStyle", source = "cleaningStyle.value")
    @Mapping(target = "rentStyle", source = "rentStyle.value")
    WgsEntity toEntity(WgsRequestDTO requestDTO);

    @Mapping(target = "cleaningStyleDescription", source = "cleaningStyle", qualifiedByName = "intToCleaningStyle")
    @Mapping(target = "rentStyleDescription",source = "rentStyle", qualifiedByName = "intToRentStyle")
    @Mapping(target = "memberNames",source = "members")
    WgsResponseDTO toResponseDTO(WgsEntity wg);

    default List<String> mapMembers(List<UsersEntity> members){
        if(members == null) return null;
        return members.stream().map(UsersEntity::getName).toList();
    }

}
