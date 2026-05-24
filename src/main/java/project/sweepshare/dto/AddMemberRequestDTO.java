package project.sweepshare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddMemberRequestDTO(
   @NotBlank
   @Email
   String email
) {}
