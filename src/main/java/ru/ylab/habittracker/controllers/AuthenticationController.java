package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.SignInRequest;
import ru.ylab.habittracker.dto.SignInResponse;
import ru.ylab.habittracker.dto.SignUpRequest;
import ru.ylab.habittracker.dto.SignUpResponse;
import ru.ylab.habittracker.services.AuthenticationService;

public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        return authenticationService.signUp(signUpRequest);
    }

    public SignInResponse signIn(SignInRequest signInRequest) {
        return authenticationService.signIn(signInRequest);
    }
}
