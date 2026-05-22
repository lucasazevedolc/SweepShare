package project.sweepshare.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TasksRequestDTO(
        @NotBlank
        String name,
        @NotNull
        Long roomId,
        @Min(1)
        @Max(5)
        Integer level
) {}
