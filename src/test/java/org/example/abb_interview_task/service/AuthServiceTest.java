package org.example.abb_interview_task.service;

import org.example.abb_interview_task.dto.JwtResponseDto;
import org.example.abb_interview_task.dto.RegisterRequest;
import org.example.abb_interview_task.entity.User;
import org.example.abb_interview_task.exception.AlreadyExistException;
import org.example.abb_interview_task.exception.NotFoundException;
import org.example.abb_interview_task.mapper.UserMapper;
import org.example.abb_interview_task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.example.abb_interview_task.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUserExists_ThrowsException() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> authService.register(registerRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");

        User user = new User();
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userMapper.requestToUser(registerRequest)).thenReturn(user);

        String result = authService.register(registerRequest);

        verify(userRepository).save(user);
        assertEquals("Register", result);
    }

    @Test
    void authenticationUserNotFound_ThrowsException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.authentication(loginRequest));
    }

    @Test
    void authenticationSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        User user = new User();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mock-jwt-token");

        JwtResponseDto response = authService.authentication(loginRequest);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("mock-jwt-token", response.getAccessToken());
    }
}
