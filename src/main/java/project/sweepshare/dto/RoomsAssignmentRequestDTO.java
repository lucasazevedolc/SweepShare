package project.sweepshare.dto;

import jakarta.validation.constraints.NotNull;

public record RoomsAssignmentRequestDTO(
        @NotNull
        Long roomId,
        @NotNull
        Long userId
) {}
