package project.sweepshare.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RoomOverviewDTO(
   Long roomId,
   String roomName,
   String currentResponsible,
   Boolean isRoomFullyCompleted,
   LocalDateTime lastTimeCleaned,
   String lastCleanedBy,
   List<TaskStatusDTO> tasks
) {}
