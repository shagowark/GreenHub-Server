package ru.greenhubserver.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.greenhubserver.dto.controller.IdDto;
import ru.greenhubserver.dto.security.JwtRequestDto;
import ru.greenhubserver.dto.security.JwtResponseDto;
import ru.greenhubserver.dto.security.RegistrationUserDto;
import ru.greenhubserver.exceptions.BadRequestException;
import ru.greenhubserver.utils.JwtTokenUtils;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public JwtResponseDto createAuthToken(JwtRequestDto authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return new JwtResponseDto(token);
    }

    @Transactional
    public IdDto createNewUser(RegistrationUserDto registrationUserDto) {
        try {
            userService.findByUsername(registrationUserDto.getUsername());
        } catch (Exception ignored){
            return new IdDto(userService.createNewUser(registrationUserDto).getId());
        }
        throw new BadRequestException("User already exists");
    }

    public JwtResponseDto createAuthToken(RegistrationUserDto registrationUserDto) {
        return createAuthToken(new JwtRequestDto(registrationUserDto.getUsername(), registrationUserDto.getPassword()));
    }
}
