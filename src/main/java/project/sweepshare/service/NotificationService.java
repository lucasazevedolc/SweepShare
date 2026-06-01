package project.sweepshare.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.sweepshare.dto.brevo.BrevoEmailRequestDTO;
import project.sweepshare.dto.brevo.BrevoRecipientDTO;
import project.sweepshare.dto.brevo.BrevoSenderDTO;

import java.util.List;

@Service
public class NotificationService {
    private final WebClient webClient;
    private final String apiKey;
    private final String apiUrl;

    public NotificationService(
            WebClient.Builder webClientBuilder,
            @Value("${brevo.api.key}") String apiKey,
            @Value("${brevo.api.url}") String apiUrl){
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public void sendEmail(List<BrevoRecipientDTO> recipients, String subject, String htmlContent){
        if(recipients == null || recipients.isEmpty()){
            return;
        }

        BrevoSenderDTO sender = new BrevoSenderDTO(
                "lucasazevedolc@gmail.com",
                "SweepShare System"
        );

        BrevoEmailRequestDTO payload = new  BrevoEmailRequestDTO(
                sender,
                recipients,
                subject,
                htmlContent
        );

        this.webClient.post()
                .uri(apiUrl)
                .header("api-key", apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> System.out.println("Email sent to Brevo " + response),
                        error -> System.out.println("Error sending email to Brevo " + error.getMessage())
                );

    }

    public void sendNewTaskScheduleEmail(String userEmail, String userName, List<String> taskNames) {
        if (taskNames == null || taskNames.isEmpty()) {
            return;
        }

        String subject = "🧼 SweepShare: Your cleaning schedule for this week!";

        StringBuilder tasksHtmlList = new StringBuilder("<ul style='font-size: 16px; color: #2e7d32; line-height: 1.6;'>");
        for (String taskName : taskNames) {
            tasksHtmlList.append("<li><strong>").append(taskName).append("</strong></li>");
        }
        tasksHtmlList.append("</ul>");

        String htmlContent = "<html><body>" +
                "<h2>Hello " + userName + "! 🚀</h2>" +
                "<p>The weekly task distribution has been executed.</p>" +
                "<p>Here is your full list of cleaning responsibilities for this week:</p>" +
                tasksHtmlList.toString() +
                "<p>Please ensure you complete your chores and hit the 'Check' button in the app for each one before Sunday ends!</p>" +
                "<br><p>Best regards,<br><strong>SweepShare System</strong></p>" +
                "</body></html>";

        BrevoRecipientDTO recipient = new BrevoRecipientDTO(userEmail, userName);
        this.sendEmail(List.of(recipient), subject, htmlContent);
    }

    public void sendNewRoomScheduleEmail(String userEmail, String userName, List<String> roomNames) {
        if (roomNames == null || roomNames.isEmpty()) {
            return;
        }

        String subject = "🧼 SweepShare: Your new cleaning assignment is here!";

        StringBuilder roomsHtmlList = new StringBuilder("<ul style='font-size: 16px; color: #2e7d32; line-height: 1.6;'>");
        for (String roomName : roomNames) {
            roomsHtmlList.append("<li><strong>").append(roomName).append("</strong></li>");
        }
        roomsHtmlList.append("</ul>");

        String htmlContent = "<html><body>" +
                "<h2>Hello " + userName + "! 🚀</h2>" +
                "<p>The weekly rotation has been executed (either automatically or forced by a flatmate).</p>" +
                "<p>Here is your full list of cleaning responsibilities for this week:</p>" +
                roomsHtmlList.toString() +
                "<p>Please ensure you complete your chores and hit the 'Check' button in the app before Sunday ends to keep your WG happy!</p>" +
                "<br><p>Best regards,<br><strong>SweepShare System</strong></p>" +
                "</body></html>";

        BrevoRecipientDTO recipient = new BrevoRecipientDTO(userEmail, userName);
        this.sendEmail(List.of(recipient), subject, htmlContent);
    }
    public void sendFixedStyleWeeklyReminder(String userEmail, String userName, String roomName) {
        String subject = "✨ New week, time to clean your room!";

        String htmlContent = "<html><body>" +
                "<h2>Hi " + userName + "! 👋</h2>" +
                "<p>A new week has officially started!</p>" +
                "<p>Since your WG uses the <strong>Fixed Room Style</strong>, your responsibility remains the same:</p>" +
                "<p style='font-size: 16px; color: #1565c0;'><strong>Your Permanent Room: " + roomName + "</strong></p>" +
                "<p>Don't forget to give it a good clean this week and log your progress in the system!</p>" +
                "<br><p>Best regards,<br><strong>SweepShare System</strong></p>" +
                "</body></html>";

        BrevoRecipientDTO recipient = new BrevoRecipientDTO(userEmail, userName);
        this.sendEmail(List.of(recipient), subject, htmlContent);
    }


}
