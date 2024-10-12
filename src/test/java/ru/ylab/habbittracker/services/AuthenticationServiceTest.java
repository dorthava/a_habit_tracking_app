package ru.ylab.habbittracker.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.habittracker.dto.SignInRequest;
import ru.ylab.habittracker.dto.SignInResponse;
import ru.ylab.habittracker.dto.SignUpRequest;
import ru.ylab.habittracker.dto.SignUpResponse;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.repositories.UsersRepositoryImpl;
import ru.ylab.habittracker.services.AuthenticationService;
import ru.ylab.habittracker.services.UsersService;
import ru.ylab.habittracker.services.UsersServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationServiceTest {
    private AuthenticationService authenticationService;
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        usersRepository = new UsersRepositoryImpl();
        UsersService usersService = new UsersServiceImpl(usersRepository, null);
        authenticationService = new AuthenticationService(usersService, usersRepository);
    }

    @Test
    void testSignUpSuccess() {
        SignUpRequest signUpRequest = new SignUpRequest("John", "john@example.com", "password");

        SignUpResponse response = authenticationService.signUp(signUpRequest);

        assertTrue(response.isSuccess());
        assertEquals("Registration successful!", response.getMessage());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void testSignUpUserAlreadyExists() {
        SignUpRequest signUpRequest = new SignUpRequest("John", "john@example.com", "password");
        usersRepository.save(new User(null, "John", "john@example.com", "password"));

        SignUpResponse response = authenticationService.signUp(signUpRequest);

        assertFalse(response.isSuccess());
        assertEquals("User already exists.", response.getMessage());
        assertNull(response.getEmail());
    }

    @Test
    void testSignInSuccess() {
        usersRepository.save(new User(1L, "John", "john@example.com", "password"));
        SignInRequest signInRequest = new SignInRequest("john@example.com", "password");

        SignInResponse response = authenticationService.signIn(signInRequest);

        assertTrue(response.isSuccess());
        assertEquals("Login successful!", response.getMessage());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void testSignInUserNotFound() {
        SignInRequest signInRequest = new SignInRequest("john@example.com", "password");

        SignInResponse response = authenticationService.signIn(signInRequest);

        assertFalse(response.isSuccess());
        assertEquals("User not found.", response.getMessage());
        assertNull(response.getEmail());
    }

    @Test
    void testSignInWrongPassword() {
        usersRepository.save(new User(1L, "John", "john@example.com", "password"));
        SignInRequest signInRequest = new SignInRequest("john@example.com", "wrongpassword");

        SignInResponse response = authenticationService.signIn(signInRequest);

        assertFalse(response.isSuccess());
        assertEquals("Wrong password.", response.getMessage());
        assertNull(response.getEmail());
    }
}
