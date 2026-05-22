package project.sweepshare.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import project.sweepshare.dto.TasksRequestDTO;
import project.sweepshare.dto.TasksResponseDTO;
import project.sweepshare.service.TasksService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tasks")
public class TaskController {
    private final TasksService tasksService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TasksResponseDTO createTask(@Valid @RequestBody TasksRequestDTO tasksRequestDTO) {
        return  tasksService.createTask(tasksRequestDTO);
    }

    @GetMapping("/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TasksResponseDTO> getByRoom(@PathVariable Long roomId) {
        return tasksService.findByRoom(roomId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TasksResponseDTO updateTask(@PathVariable Long id, @RequestBody TasksRequestDTO dto) {
        return tasksService.updateTask(dto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        tasksService.deleteTask(id);
    }

}
