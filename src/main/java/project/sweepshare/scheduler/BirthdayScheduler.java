package project.sweepshare.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.dto.brevo.BrevoRecipientDTO;
import project.sweepshare.service.NotificationService;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BirthdayScheduler {
    private final IUsersRepository  usersRepository;
    private final NotificationService notificationService;

    public BirthdayScheduler(IUsersRepository usersRepository, NotificationService notificationService){
        this.usersRepository = usersRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 6 * * *")
    //@Scheduled(fixedDelay = 30000)
    @Transactional(readOnly = true)
    public void checkAndSendBirthday(){
        System.out.println("Checking Birthdays");

        MonthDay today = MonthDay.now();
        MonthDay nextWeek = MonthDay.from(LocalDate.now().plusDays(7));

        List<UsersEntity> allUsers = usersRepository.findAll();

        for(UsersEntity user : allUsers){
            if(user.getBirthday() != null && user.getWg() != null){
                MonthDay userBirthday = MonthDay.from(user.getBirthday());
                WgsEntity wg =  user.getWg();

                List<BrevoRecipientDTO> flatmates = wg.getMembers().stream()
                        .filter(member -> !member.getEmail().equalsIgnoreCase(user.getEmail()))
                        .map(member -> new BrevoRecipientDTO(member.getEmail(), member.getName()))
                        .collect(Collectors.toList());

                if(flatmates.isEmpty()) continue;

                if(userBirthday.equals(today)){

                    String subject = "🎉 Birthday Alert in your WG: " + user.getName() + "'s birthday is today!";
                    String htmlContent = "<html><body>" +
                            "<h3>Hey Flatmate! 🎂</h3>" +
                            "<p>Today is the day! <strong>" + user.getName() + "</strong> is celebrating another year of life!</p>" +
                            "<p>Don't forget to congratulate them and maybe plan a nice dinner or a small celebration in the WG tonight! 😉</p>" +
                            "<br><p>Best regards,<br><strong>SweepShare System</strong></p>" +
                            "</body></html>";

                    notificationService.sendEmail(flatmates, subject, htmlContent);
                    System.out.println("Today's birthday notification sent" + user.getName());

                    List<BrevoRecipientDTO> birthdayUserList = List.of(new BrevoRecipientDTO(user.getEmail(), user.getName()));
                    String userSubject = "🎂 Happy Birthday from SweepShare, " + user.getName() + "!";
                    String userHtmlContent = "<html><body>" +
                            "<h2>Happy Birthday, " + user.getName() + "! 🎉🎈</h2>" +
                            "<p>The entire SweepShare team wishes you an amazing day filled with joy, peace, and cake! 🍰</p>" +
                            "<p>Thank you for being part of our community and helping keep your WG organized. Enjoy your special day to the fullest!</p>" +
                            "<br><p>Warmest regards,<br><strong>The SweepShare Team 🧼✨</strong></p>" +
                            "</body></html>";
                    notificationService.sendEmail(birthdayUserList, userSubject, userHtmlContent);

                }
                else if(userBirthday.equals(nextWeek)) {
                    String subject = "⏳ Heads up! " + user.getName() + "'s birthday is in 1 week!";
                    String htmlContent = "<html><body>" +
                            "<h3>Hey Flatmate! 🎁</h3>" +
                            "<p>Just a friendly heads-up: <strong>" + user.getName() + "</strong>'s birthday is coming up in exactly <strong>one week</strong>!</p>" +
                            "<p>This gives you plenty of time to organize a group gift, baked treats, or coordinate with the other housemates so the day doesn't catch you by surprise. 🍰🎈</p>" +
                            "<br><p>Best regards,<br><strong>SweepShare System</strong></p>" +
                            "</body></html>";

                    notificationService.sendEmail(flatmates, subject, htmlContent);
                    System.out.println("1-week advance birthday notification sent. " + user.getName());
                }
            }
        }
    }
}
