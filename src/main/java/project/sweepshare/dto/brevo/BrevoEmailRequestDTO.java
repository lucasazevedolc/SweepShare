package project.sweepshare.dto.brevo;

import java.util.List;

public record BrevoEmailRequestDTO(
   BrevoSenderDTO sender,
   List<BrevoRecipientDTO> to,
   String subject,
   String htmlContent
) {}
