package project.sweepshare.dto;

import java.util.List;

public record WgCleaningStatusResponseDTO(
   Long wgId,
   String cleaningStyle,
   List<RoomOverviewDTO> rooms
) {}
