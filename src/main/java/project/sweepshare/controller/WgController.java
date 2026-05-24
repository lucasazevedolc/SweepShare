package project.sweepshare.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.sweepshare.dto.AddMemberRequestDTO;
import project.sweepshare.dto.WgsRequestDTO;
import project.sweepshare.dto.WgsResponseDTO;
import project.sweepshare.service.RoomRotationService;
import project.sweepshare.service.WgsService;

import java.security.Principal;

@RestController
@RequestMapping("/v1/wgs")
@RequiredArgsConstructor
@Validated
public class WgController {
    private final WgsService wgsService;
    private final RoomRotationService roomRotationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WgsResponseDTO createWg(@Valid @RequestBody WgsRequestDTO dto){
       return wgsService.create(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WgsResponseDTO updateWg(@RequestBody WgsRequestDTO dto, @PathVariable Long id){
        return wgsService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveWg(@PathVariable Long id){
        wgsService.leaveWg(id);
    }

    @PostMapping("/add-member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMember(@Valid @RequestBody AddMemberRequestDTO dto, Principal principal){
        String loggedUserEmail = principal.getName();

        wgsService.addMemberByEmail(loggedUserEmail, dto);
    }

    @PostMapping("/{id}/rotate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forceRoomRotation(@PathVariable Long id, Principal principal){
        String loggedUserEmail = principal.getName();
        roomRotationService.forceRotation(id,loggedUserEmail);
    }
}
