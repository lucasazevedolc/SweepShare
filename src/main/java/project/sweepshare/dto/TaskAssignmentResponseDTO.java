package project.sweepshare.dto;

public record TaskAssignmentResponseDTO(
   Long id,
   Long taskId,
   String taskName,
   Integer taskLevel,
   Long roomId,
   String roomName,
   Long userId,
   String userName
) {}
