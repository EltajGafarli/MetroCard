package org.example.abb_interview_task.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.abb_interview_task.dto.JwtResponseDto;
import org.example.abb_interview_task.dto.LoginRequest;
import org.example.abb_interview_task.dto.RegisterRequest;
import org.example.abb_interview_task.entity.Role;
import org.example.abb_interview_task.entity.RoleEnum;
import org.example.abb_interview_task.entity.User;
import org.example.abb_interview_task.exception.AlreadyExistException;
import org.example.abb_interview_task.exception.NotFoundException;
import org.example.abb_interview_task.mapper.UserMapper;
import org.example.abb_interview_task.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;


    @Transactional(rollbackOn = Exception.class)
    public String register(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent((data) -> {
                    throw new AlreadyExistException("User already exist!!!");
                });

        User user = userMapper.requestToUser(registerRequest);

        user.addRole(
                Role
                        .builder()
                        .roleEnum(RoleEnum.USER)
                        .build()
        );

        user.setEnabled(true);

        userRepository.save(user);
        return "Register";
    }


    public JwtResponseDto authentication(LoginRequest loginRequest) {

        userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        String email = loginRequest.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found")
        );


        String jwt = jwtService.generateToken(user);
        return getToken(jwt);

    }

    private JwtResponseDto getToken(String jwt) {
        return JwtResponseDto.builder()
                .accessToken(jwt)
                .build();
    }


}
