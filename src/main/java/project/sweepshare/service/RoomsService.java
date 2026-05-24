package project.sweepshare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.RoomsAssignmentsEntity;
import project.sweepshare.database.model.RoomsEntity;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.repository.IRoomsAssignmentsRepository;
import project.sweepshare.database.repository.IRoomsRepository;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.dto.RoomsRequestDTO;
import project.sweepshare.dto.RoomsResponseDTO;
import project.sweepshare.mapper.IRoomsMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomsService {

    private final IRoomsRepository roomsRepository;
    private final IUsersRepository usersRepository;
    private final IRoomsAssignmentsRepository assignmentsRepository;
    private final IRoomsMapper roomsMapper;
    private final RoomRotationService roomRotationService;

    @Transactional
    public RoomsResponseDTO create(RoomsRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        if(user.getWg() == null){
            throw new RuntimeException("You are not part of a WG");
        }

        if (roomsRepository.existsByNameIgnoreCaseAndWgId(dto.name(), user.getWg().getId())) {
            throw new RuntimeException("Room already exists");
        }

        RoomsEntity room = roomsMapper.toEntity(dto);
        room.setWg(user.getWg());

        if(room.getFrequency()==null || room.getFrequency() == 0){
            room.setFrequency(7);
        }

        RoomsEntity savedRoom = roomsRepository.save(room);

        RoomsAssignmentsEntity assignment = RoomsAssignmentsEntity.builder()
                .room(room)
                .user(user)
                .build();

        assignmentsRepository.save(assignment);

        roomRotationService.rotateSingleWg(room.getWg());

        return roomsMapper.toResponseDTO(savedRoom);

        }

    @Transactional(readOnly = true)
    public List<RoomsResponseDTO> findAllFromMyWg(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        if(user.getWg() == null){
            throw new RuntimeException("You are not part of a WG");
        }

        List<RoomsEntity> rooms = roomsRepository.findByWgId(user.getWg().getId());

        return roomsMapper.toResponseDTO(rooms);
    }

    @Transactional
    public RoomsResponseDTO update(RoomsRequestDTO dto, Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        if(user.getWg() == null){
            throw new RuntimeException("You are not part of a WG");
        }

        RoomsEntity room = roomsRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Room not found"));

        if(dto.name() != null && !dto.name().isBlank()){
            room.setName(dto.name());
        }

        if(dto.frequency() != null){
            room.setFrequency(dto.frequency());
        }

        RoomsEntity savedRoom = roomsRepository.save(room);
        return roomsMapper.toResponseDTO(savedRoom);
    }

    @Transactional
    public void delete(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        RoomsEntity room = roomsRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Room not found"));

        if(user.getWg() == null || !user.getWg().getId().equals(room.getWg().getId())){
            throw new RuntimeException("You don't have permission to delete this room");
        }

        roomsRepository.delete(room);
    }
}
