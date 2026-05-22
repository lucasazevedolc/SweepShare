package project.sweepshare.dto;

import jakarta.validation.constraints.NotNull;

public record TaskAssignmentRequestDTO(
   @NotNull
   Long taskId,
   @NotNull
   Long userId
) {}
