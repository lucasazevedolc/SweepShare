package project.sweepshare.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.dto.LoginRequestDTO;
import project.sweepshare.dto.LoginResponseDTO;
import project.sweepshare.dto.RefreshTokenRequestDTO;
import project.sweepshare.exception.AccessDeniedException;
import project.sweepshare.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final IUsersRepository usersRepository;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.email());
        String token =  jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        UsersEntity user = usersRepository.findByEmail(dto.email())
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        user.setRefreshToken(refreshToken);
        usersRepository.save(user);

        return new LoginResponseDTO(token, refreshToken);
    }

    @Transactional
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO dto) {

        String email = jwtService.extractUsername(dto.refreshToken());
        UserDetails userDetails =  userDetailsService.loadUserByUsername(email);

        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        if(!jwtService.isRefreshTokenValid(dto.refreshToken(), userDetails)) {
            throw new AccessDeniedException("Invalid refresh token");
        }

        if(user.getRefreshToken() == null || !user.getRefreshToken().equals(dto.refreshToken())) {
            throw new AccessDeniedException("Invalid refresh token");
        }

        String newToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        user.setRefreshToken(newRefreshToken);
        usersRepository.save(user);

        return new LoginResponseDTO(newToken, newRefreshToken);
    }

    @Transactional
    public void logout(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        user.setRefreshToken(null);
        usersRepository.save(user);
    }
}
