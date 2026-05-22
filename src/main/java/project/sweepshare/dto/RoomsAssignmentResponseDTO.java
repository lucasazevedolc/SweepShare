package project.sweepshare.dto;

public record RoomsAssignmentResponseDTO(
   Long id,
   Long roomId,
   String roomName,
   Long userId,
   String userName
) {}
