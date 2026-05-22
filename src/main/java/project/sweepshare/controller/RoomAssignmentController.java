package project.sweepshare.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import project.sweepshare.dto.RoomsAssignmentRequestDTO;
import project.sweepshare.dto.RoomsAssignmentResponseDTO;
import project.sweepshare.service.RoomsAssignmentsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/room-assignments")
public class RoomAssignmentController {
    private final RoomsAssignmentsService assignmentsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomsAssignmentResponseDTO assignRoom(@Valid @RequestBody RoomsAssignmentRequestDTO dto) {
        return assignmentsService.assignRoom(dto);
    }

    @GetMapping("/my-wg")
    @ResponseStatus(HttpStatus.OK)
    public List<RoomsAssignmentResponseDTO> getMyWgScale() {
        return assignmentsService.getMyWgScale();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssignment(@PathVariable Long id){
        assignmentsService.removeAssignment(id);
    }
}
