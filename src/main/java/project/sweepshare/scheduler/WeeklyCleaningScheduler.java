package project.sweepshare.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.RoomsAssignmentsEntity;
import project.sweepshare.database.model.TasksAssignmentsEntity;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;
import project.sweepshare.database.repository.IRoomsAssignmentsRepository;
import project.sweepshare.database.repository.ITasksAssignmentsRepository;
import project.sweepshare.database.repository.IWgsRepository;
import project.sweepshare.dto.brevo.BrevoRecipientDTO;
import project.sweepshare.service.NotificationService;

import java.util.List;

@Component
public class WeeklyCleaningScheduler {
    private final IWgsRepository wgsRepository;
    private final IRoomsAssignmentsRepository roomsAssignmentsRepository;
    private final ITasksAssignmentsRepository tasksAssignmentsRepository;
    private final NotificationService notificationService;

    public WeeklyCleaningScheduler(
            IWgsRepository wgsRepository,
            IRoomsAssignmentsRepository roomsAssignmentsRepository,
            ITasksAssignmentsRepository tasksAssignmentsRepository,
            NotificationService notificationService) {
        this.wgsRepository = wgsRepository;
        this.roomsAssignmentsRepository = roomsAssignmentsRepository;
        this.tasksAssignmentsRepository = tasksAssignmentsRepository;
        this.notificationService = notificationService;
    }

    //@Scheduled(fixedDelay = 30000)
    @Scheduled(cron = "0 59 23 * * SUN")
    @Transactional(readOnly = true)
    public void sendFixedStyleWeeklyNotification() {
        System.out.println("Executing weekly notification for FIXED style WGs...");

        List<WgsEntity> allWgs = wgsRepository.findAll();

        for (WgsEntity wg : allWgs) {
            if (wg.getCleaningStyle() == 0) {

                List<RoomsAssignmentsEntity> wgAssignments = roomsAssignmentsRepository.findByRoomWgId(wg.getId());

                for (UsersEntity member : wg.getMembers()) {
                    String assignedRoomName = wgAssignments.stream()
                            .filter(assignment -> assignment.getUser() != null && assignment.getUser().getId().equals(member.getId()))
                            .map(assignment -> assignment.getRoom() != null ? assignment.getRoom().getName() : null)
                            .findFirst()
                            .orElse("your assigned area");

                    notificationService.sendFixedStyleWeeklyReminder(member.getEmail(), member.getName(), assignedRoomName);
                }
            }
        }
    }

    //@Scheduled(fixedDelay = 30000)
    @Scheduled(cron = "0 0 12 * * THU")
    @Transactional(readOnly = true)
    public void sendMidWeekReminder() {
        System.out.println("Executing Mid-Week pending chores reminder...");

        List<RoomsAssignmentsEntity> pendingRooms = roomsAssignmentsRepository.findByIsCompletedFalse();

        for (RoomsAssignmentsEntity assignment : pendingRooms) {
            UsersEntity user = assignment.getUser();

            if (user != null && user.getWg() != null) {
                int currentStyle = user.getWg().getCleaningStyle();

                if (currentStyle == 0 || currentStyle == 1) { // 0: FIXED_PER_ROOM, 1: WEEKLY_ROTATION
                    sendReminderEmail(user, "room: " + assignment.getRoom().getName());
                }
            }
        }

        List<TasksAssignmentsEntity> pendingTasks = tasksAssignmentsRepository.findByIsCompletedFalse();

        for (TasksAssignmentsEntity assignment : pendingTasks) {
            UsersEntity user = assignment.getUser();

            if (user != null && user.getWg() != null) {
                int currentStyle = user.getWg().getCleaningStyle();

                if (currentStyle == 2) { // 2: TASK_AMOUNT
                    sendReminderEmail(user, "task: " + assignment.getTask().getName());
                }

            }
        }

    }

    private void sendReminderEmail(UsersEntity user, String choreDescription) {
        String subject = "⏰ Quick Reminder: Your SweepShare task is waiting for you!";
        String htmlContent = "<html><body>" +
                "<h3>Hello " + user.getName() + ", just a friendly reminder! 🧼</h3>" +
                "<p>We hope you are having a great week.</p>" +
                "<p>This is a quick system check to remind you that your cleaning chore for this week (<strong>" + choreDescription + "</strong>) is still pending.</p>" +
                "<p>Please complete it whenever you can and remember to mark it as done in the application before Sunday ends.</p>" +
                "<br><p>Thank you for keeping your WG clean and cozy! 🌟<br><strong>SweepShare System</strong></p>" +
                "</body></html>";

        BrevoRecipientDTO recipient = new BrevoRecipientDTO(user.getEmail(), user.getName());
        notificationService.sendEmail(List.of(recipient), subject, htmlContent);
    }
}


