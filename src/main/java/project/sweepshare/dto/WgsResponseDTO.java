package project.sweepshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WgsResponseDTO(
        @NotNull
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String cleaningStyleDescription,
        String rentStyleDescription,
        List<String> memberNames
) {}
