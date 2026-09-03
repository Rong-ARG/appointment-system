package com.ronogar.appointment_system.services.auth;

import com.ronogar.appointment_system.dtos.auth.AuthRequestDTO;
import com.ronogar.appointment_system.dtos.auth.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        authRequestDTO.getPassword()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String token = jwtService.createToken(authentication);

        return new AuthResponseDTO(token);
    }


}
