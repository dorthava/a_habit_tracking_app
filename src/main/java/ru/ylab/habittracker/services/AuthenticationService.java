package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.SignInRequest;
import ru.ylab.habittracker.dto.SignInResponse;
import ru.ylab.habittracker.dto.SignUpRequest;
import ru.ylab.habittracker.dto.SignUpResponse;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.UsersRepository;

import java.util.Optional;

public class AuthenticationService {
    private final UsersService usersService;
    private final UsersRepository usersRepository;

    public AuthenticationService(UsersService usersService, UsersRepository usersRepository) {
        this.usersService = usersService;
        this.usersRepository = usersRepository;
    }

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        if (usersRepository.existsByEmail(signUpRequest.getEmail())) {
            return new SignUpResponse(false, "User already exists.", null);
        }
        User user = new User(null, signUpRequest.getName(),
                signUpRequest.getEmail(), signUpRequest.getPassword());
        usersService.create(user);
        return new SignUpResponse(true, "Registration successful!", user.getId());
    }

    public SignInResponse signIn(SignInRequest signInRequest) {
        Optional<User> userOptional = usersRepository.findByEmail(signInRequest.getEmail());
        if (userOptional.isEmpty()) {
            return new SignInResponse(false, "User not found.", null);
        }
        User user = userOptional.get();
        if (!user.getPassword().equals(signInRequest.getPassword())) {
            return new SignInResponse(false, "Wrong password.", null);
        }
        return new SignInResponse(true, "Login successful!", userOptional.get().getId());
    }
}
