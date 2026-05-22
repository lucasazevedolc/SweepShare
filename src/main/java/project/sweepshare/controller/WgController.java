package project.sweepshare.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.sweepshare.dto.WgsRequestDTO;
import project.sweepshare.dto.WgsResponseDTO;
import project.sweepshare.service.WgsService;

@RestController
@RequestMapping("/v1/wgs")
@RequiredArgsConstructor
@Validated
public class WgController {
    private final WgsService wgsService;

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
}
