package project.sweepshare.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import project.sweepshare.dto.LoginRequestDTO;
import project.sweepshare.dto.LoginResponseDTO;
import project.sweepshare.dto.RefreshTokenRequestDTO;
import project.sweepshare.security.AuthService;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return  authService.login(dto);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDTO refreshToken(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        return  authService.refreshToken(dto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        authService.logout();
    }

}
