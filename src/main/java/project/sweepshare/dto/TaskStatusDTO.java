package project.sweepshare.dto;

public record TaskStatusDTO(
   Long taskId,
   String taskName,
   Integer level,
   Boolean isCompleted
) {}
