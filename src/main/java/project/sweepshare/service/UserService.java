package project.sweepshare.service;

import lombok.RequiredArgsConstructor;


import org.springframework.data.crossstore.ChangeSetPersister;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.dto.UpdatePasswordDTO;
import project.sweepshare.dto.UsersRequestDTO;
import project.sweepshare.dto.UsersResponseDTO;
import project.sweepshare.exception.AccessDeniedException;
import project.sweepshare.exception.BadRequestException;
import project.sweepshare.exception.ResourceNotFoundException;
import project.sweepshare.mapper.IUsersMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUsersRepository usersRepository;
    private final IUsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;

    public UsersResponseDTO createUser(UsersRequestDTO requestDTO) {
        UsersEntity userEntity = usersMapper.toEntity(requestDTO);

        userEntity.setPassword(passwordEncoder.encode(requestDTO.password()));

        return usersMapper.toResponseDTO(usersRepository.save(userEntity));
    }

    public List<UsersResponseDTO> getAllUsers() {
        List<UsersEntity> list = usersRepository.findAll();
        List<UsersResponseDTO> responseDTOList = new ArrayList<>();
        for (UsersEntity userEntity : list) {
            responseDTOList.add(usersMapper.toResponseDTO(userEntity));
        }
        return responseDTOList;
    }

    public UsersResponseDTO getUserById(Long userId) throws ChangeSetPersister.NotFoundException {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        return usersMapper.toResponseDTO(user);
    }

    public UsersResponseDTO updateUser(Long userId, UsersRequestDTO requestDTO) throws ChangeSetPersister.NotFoundException {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        usersMapper.updateEntityFromDto(requestDTO, user);

        return usersMapper.toResponseDTO(usersRepository.save(user));
    }

    public UsersResponseDTO getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        return usersMapper.toResponseDTO(user);
    }

    public void updatePassword(Long id, UpdatePasswordDTO dto){
        UsersEntity user =  usersRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        String loggedData = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!user.getEmail().equals(loggedData)){
            throw new AccessDeniedException("Access Denied");
        }

        if(!dto.newPassword().equals(dto.confirmPassword())){
            throw new BadRequestException("The passwords do not match");
        }

        if(!passwordEncoder.matches(dto.oldPassword(),user.getPassword())){
            throw new BadRequestException("Your current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        usersRepository.save(user);
    }

    public UsersResponseDTO getUserByEmail(String email) throws ChangeSetPersister.NotFoundException {
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return usersMapper.toResponseDTO(user);
    }

    public UsersResponseDTO findUser(String userData){
        UsersEntity user = usersRepository.findByEmail(userData).orElse(null);
        if(user != null) return usersMapper.toResponseDTO(user);

        user = usersRepository.findByName(userData).orElse(null);
        if(user != null) return usersMapper.toResponseDTO(user);

        try {
            Long parsedId = Long.parseLong(userData);

            user = usersRepository.findById(parsedId).orElse(null);
            if (user != null) return usersMapper.toResponseDTO(user);
        }
        catch (NumberFormatException e) {}

        throw new ResourceNotFoundException("User not found");
    }

    public void deactivateUser(Long id){
        UsersEntity user = usersRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        String loggedData = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if(!user.getEmail().equals(loggedData) || !isAdmin) throw new  AccessDeniedException("Access Denied");

        user.setActive(false);
        usersRepository.save(user);
    }
}
