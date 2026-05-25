package project.sweepshare.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import project.sweepshare.service.CleaningManagementService;

@Component
@RequiredArgsConstructor
public class CleaningManagementScheduler {
    private final CleaningManagementService cleaningManagementService;

    @Scheduled(cron = "0 0 0 * * SUN") //(fixedRate = 60000)
    private void executeWeeklyRoomRotation() {
        cleaningManagementService.executeAllCleaningStrategies();
    }
}
