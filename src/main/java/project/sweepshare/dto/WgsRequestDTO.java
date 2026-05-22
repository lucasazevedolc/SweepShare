package project.sweepshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import project.sweepshare.enums.CleaningStyle;
import project.sweepshare.enums.RentStyle;

import java.time.LocalDate;

public record WgsRequestDTO(
   @NotBlank
   String name,
   @NotNull
   CleaningStyle cleaningStyle,
   RentStyle rentStyle
) {}
