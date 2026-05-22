package project.sweepshare.dto;

public record LoginResponseDTO(
        String token,
        String refreshToken
) {}
