package project.sweepshare.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import project.sweepshare.dto.TaskAssignmentRequestDTO;
import project.sweepshare.dto.TaskAssignmentResponseDTO;
import project.sweepshare.service.TaskAssignmentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/task-assignments")
public class TaskAssignmentController {
    private final TaskAssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskAssignmentResponseDTO assignTask(@Valid @RequestBody TaskAssignmentRequestDTO dto) {
        return assignmentService.createAssignment(dto);
    }

    @GetMapping("/my-wg")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskAssignmentResponseDTO> getMyWgTaskScale(){
        return assignmentService.getMyWgTaskScale();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssignment(@PathVariable Long id){
        assignmentService.removeAssignment(id);
    }
}
