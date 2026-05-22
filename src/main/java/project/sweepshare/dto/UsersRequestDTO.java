package project.sweepshare.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UsersRequestDTO(
   @NotBlank
   String name,
   @NotBlank
   String email,
   @NotBlank
   String password,
   @JsonFormat(pattern ="dd.MM.yyyy")
   LocalDate birthday
) {}
