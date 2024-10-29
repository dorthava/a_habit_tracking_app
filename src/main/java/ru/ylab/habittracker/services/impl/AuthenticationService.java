package ru.ylab.habittracker.services.impl;

import ru.ylab.habittracker.dto.*;
import ru.ylab.habittracker.models.Users;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.services.UsersService;
import ru.ylab.habittracker.utils.Role;

import java.util.Optional;

/**
 * Сервис аутентификации пользователей.
 * Этот класс предоставляет методы для регистрации и входа пользователей в систему.
 */
public class AuthenticationService {
    private final UsersService usersService;
    private final UsersRepository usersRepository;

    /**
     * Конструктор класса AuthenticationService.
     *
     * @param usersService сервис для управления пользователями.
     * @param usersRepository репозиторий для доступа к данным пользователей.
     */
    public AuthenticationService(UsersService usersService, UsersRepository usersRepository) {
        this.usersService = usersService;
        this.usersRepository = usersRepository;
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param signUpRequest объект запроса для регистрации, содержащий имя, email и пароль пользователя.
     * @return BaseResponse<UserResponse> объект ответа, содержащий информацию о зарегистрированном пользователе.
     * @throws RuntimeException если создание пользователя не удалось.
     */
    public BaseResponse<UserResponse> signUp(SignUpRequest signUpRequest) {
        Users users = new Users(null, signUpRequest.getName(),
                signUpRequest.getEmail(), signUpRequest.getPassword(), Role.USER, false);
        users = usersService.create(users);
        if (users == null) {
            throw new RuntimeException("Users creation failed");
        }
        UserResponse userResponse = new UserResponse(users.getId(), users.getName(), users.getEmail());
        return new BaseResponse<>("User created", userResponse);
    }

    /**
     * Позволяет пользователю войти в систему.
     *
     * @param signInRequest объект запроса для входа, содержащий email и пароль пользователя.
     * @return BaseResponse<UserResponse> объект ответа, содержащий информацию о вошедшем пользователе.
     * @throws RuntimeException если пользователь не найден или пароль неверный.
     */
    public BaseResponse<UserResponse> signIn(SignInRequest signInRequest) {
        Optional<Users> userOptional = usersRepository.findByEmail(signInRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Users users = userOptional.get();
        if (!users.getPassword().equals(signInRequest.getPassword())) {
            throw new RuntimeException("Wrong password");
        }
        UserResponse userResponse = new UserResponse(users.getId(), users.getName(), users.getEmail());
        return new BaseResponse<>("User successfully logged in", userResponse);
    }
}
