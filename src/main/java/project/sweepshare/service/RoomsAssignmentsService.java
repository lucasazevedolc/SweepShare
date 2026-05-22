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
import project.sweepshare.dto.RoomsAssignmentRequestDTO;
import project.sweepshare.dto.RoomsAssignmentResponseDTO;
import project.sweepshare.mapper.IRoomsAssignmentsMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomsAssignmentsService {
    private final IRoomsAssignmentsRepository assignmentsRepository;
    private final IRoomsRepository roomsRepository;
    private final IUsersRepository usersRepository;
    private final IRoomsAssignmentsMapper assignmentsMapper;

    @Transactional
    public RoomsAssignmentResponseDTO assignRoom(RoomsAssignmentRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoomsEntity room = roomsRepository.findById(dto.roomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        UsersEntity targetUser = usersRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userWgId = user.getWg().getId() != null ? user.getWg().getId() : null;
        Long roomWgId = room.getWg().getId() != null ? room.getWg().getId() : null;
        Long targetWgId = targetUser.getWg().getId() != null ? targetUser.getWg().getId() : null;

        if (userWgId == null || !userWgId.equals(roomWgId) || !userWgId.equals(targetWgId)) {
            throw new RuntimeException("Access denied");
        }

        if (assignmentsRepository.existsByRoomId(dto.roomId())) {
            throw new RuntimeException("This room is already assigned");
        }

        RoomsAssignmentsEntity assignment = RoomsAssignmentsEntity.builder()
                .room(room)
                .user(targetUser)
                .build();

        RoomsAssignmentsEntity savedAssignment = assignmentsRepository.save(assignment);
        return assignmentsMapper.toResponseDTO(savedAssignment);

    }

    @Transactional(readOnly = true)
    public List<RoomsAssignmentResponseDTO> getMyWgScale(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getWg() == null){
            throw new RuntimeException("You are not part of a WG");
        }

        List<RoomsAssignmentsEntity> assignments = assignmentsRepository.findByRoomWgId(user.getWg().getId());
        return assignmentsMapper.toResponseDTOList(assignments);
    }

    @Transactional
    public void removeAssignment(Long id){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoomsAssignmentsEntity assignment = assignmentsRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("WG assignment not found"));

        if(user.getWg() == null || !assignment.getRoom().getWg().getId().equals(user.getWg().getId())){
            throw new RuntimeException("You don't have permission to remove this assignment");
        }

        assignmentsRepository.delete(assignment);
    }
}


