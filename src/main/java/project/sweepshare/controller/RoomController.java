package project.sweepshare.controller;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import project.sweepshare.dto.RoomsRequestDTO;
import project.sweepshare.dto.RoomsResponseDTO;
import project.sweepshare.service.RoomsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rooms")
public class RoomController {
    private final RoomsService roomsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomsResponseDTO createRoom(@Valid @RequestBody RoomsRequestDTO dto) {
        return roomsService.create(dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoomsResponseDTO> getAllFromMyWg() {
        return roomsService.findAllFromMyWg();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoomsResponseDTO updateRoom(@RequestBody RoomsRequestDTO dto, @PathVariable Long id) {
        return roomsService.update(dto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Long id) {
        roomsService.delete(id);
    }

}
