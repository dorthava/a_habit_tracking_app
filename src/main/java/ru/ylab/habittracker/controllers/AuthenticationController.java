package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.*;
import ru.ylab.habittracker.services.impl.AuthenticationService;

public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public BaseResponse<UserResponse> signUp(SignUpRequest signUpRequest) {
        BaseResponse<UserResponse> baseResponse;
        try {
            baseResponse = authenticationService.signUp(signUpRequest);
        } catch (RuntimeException e) {
            baseResponse = new BaseResponse<>(e.getMessage(), null);
        }
        return baseResponse;
    }

    public BaseResponse<UserResponse> signIn(SignInRequest signInRequest) {
        BaseResponse<UserResponse> baseResponse;
        try {
            baseResponse = authenticationService.signIn(signInRequest);
        } catch (RuntimeException e) {
            baseResponse = new BaseResponse<>(e.getMessage(), null);
        }
        return baseResponse;
    }
}
