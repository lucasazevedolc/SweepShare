package project.sweepshare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.sweepshare.dto.UpdatePasswordDTO;
import project.sweepshare.dto.UsersRequestDTO;
import project.sweepshare.dto.UsersResponseDTO;
import project.sweepshare.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@Validated @RequestBody UsersRequestDTO requestDTO) {
        userService.createUser(requestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UsersResponseDTO> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UsersResponseDTO getUserById(@PathVariable Long userId) throws ChangeSetPersister.NotFoundException {

        return userService.getUserById(userId);
    }

    @GetMapping("/find/{userData}")
    @ResponseStatus(HttpStatus.OK)
    public UsersResponseDTO findUser(@PathVariable String userData) throws ChangeSetPersister.NotFoundException {

        return userService.findUser(userData);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable Long userId, @Validated @RequestBody UsersRequestDTO requestDTO) throws ChangeSetPersister.NotFoundException {
        userService.updateUser(userId, requestDTO);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UsersResponseDTO getMyProfile(){
        return userService.getCurrentUser();
    }

    @PatchMapping("{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@PathVariable Long id, @Validated @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        userService.updatePassword(id, updatePasswordDTO);
    }

    @GetMapping("/email/{userEmail}")
    @ResponseStatus(HttpStatus.OK)
    public UsersResponseDTO getUserById(@PathVariable String userEmail) throws ChangeSetPersister.NotFoundException {

        return userService.getUserByEmail(userEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        userService.deactivateUser(id);
    }

}
