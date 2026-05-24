package project.sweepshare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.database.repository.IWgsRepository;
import project.sweepshare.dto.AddMemberRequestDTO;
import project.sweepshare.dto.WgsRequestDTO;
import project.sweepshare.dto.WgsResponseDTO;
import project.sweepshare.mapper.IWgsMapper;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class WgsService {
    private final IWgsRepository wgsRepository;
    private final IUsersRepository usersRepository;
    private final IWgsMapper wgsMapper;

    @Transactional
    public WgsResponseDTO create(WgsRequestDTO dto) {
        String  email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        if(user.getWg() != null){
            throw new RuntimeException("You already have a WG");
        }

        WgsEntity wg = WgsEntity.builder()
                .name(dto.name())
                .cleaningStyle(dto.cleaningStyle().getValue())
                .rentStyle(dto.rentStyle().getValue())
                .build();

        WgsEntity savedWg = wgsRepository.save(wg);

        user.setWg(savedWg);
        usersRepository.save(user);

        savedWg.getMembers().add(user);

        return wgsMapper.toResponseDTO(savedWg);
    }

    @Transactional
    public WgsResponseDTO update(Long id, WgsRequestDTO dto) {
        String  email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity  user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        WgsEntity wg = wgsRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("WG not found"));

        if(user.getWg() == null || !user.getWg().getId().equals(id)) {
            throw new  RuntimeException("You don't have permission to update this resource");
        }

        if(dto.name() != null && !dto.name().isBlank()) {
            wg.setName(dto.name());
        }
        if(dto.cleaningStyle() != null){
            wg.setCleaningStyle(dto.cleaningStyle().getValue());
        }
        if(dto.rentStyle() != null){
            wg.setRentStyle(dto.rentStyle().getValue());
        }
        WgsEntity updatedWg = wgsRepository.save(wg);
        return wgsMapper.toResponseDTO(updatedWg);
    }

    @Transactional
    public void leaveWg(Long id) {
        String  email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity  user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getWg() == null || !user.getWg().getId().equals(id)) {
            throw new  RuntimeException("You don't have permission to update this resource");
        }

        WgsEntity wg = user.getWg();

        user.setWg(null);
        usersRepository.save(user);

        if(usersRepository.countByWg(wg) == 0) {
            wgsRepository.delete(wg);
        }
    }

    @Transactional
    public void addMemberByEmail(String creatorEmail, AddMemberRequestDTO dto){
        String  email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity  creator = usersRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Not found"));

        WgsEntity wg = creator.getWg();
        if (wg == null) {
            throw new RuntimeException("You must belong to a WG to invite other members");
        }

        UsersEntity targetUser = usersRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("No user found with the provided email"));

        if (targetUser.getWg() != null) {
            throw new RuntimeException("The user is already a member of a WG");
        }

        targetUser.setWg(wg);
        usersRepository.save(targetUser);
    }

}
