package project.sweepshare.dto;

public record TasksResponseDTO(
    Long id,
    String name,
    Integer level,
    Long roomId,
    String roomName
) {}
