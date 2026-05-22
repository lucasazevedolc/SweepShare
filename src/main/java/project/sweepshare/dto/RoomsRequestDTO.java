package project.sweepshare.dto;

import jakarta.validation.constraints.NotBlank;

public record RoomsRequestDTO(
        @NotBlank
        String name,
        Integer frequency
) {}
