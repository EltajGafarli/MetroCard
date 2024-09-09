package org.example.abb_interview_task.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.abb_interview_task.dto.JwtResponseDto;
import org.example.abb_interview_task.dto.LoginRequest;
import org.example.abb_interview_task.dto.RegisterRequest;
import org.example.abb_interview_task.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        log.info(request.toString());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        authService.register(request)
                );
    }

    @PostMapping(path = "/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.authentication(loginRequest));
    }
}