package project.sweepshare.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import project.sweepshare.service.RoomRotationService;

@Component
@RequiredArgsConstructor
public class RoomRotationScheduler {
    private final RoomRotationService rotationService;

    @Scheduled(cron = "0 0 0 * * SUN") //(fixedRate = 60000)
    private void executeWeeklyRoomRotation() {
        rotationService.rotateAllWgs();
    }
}
