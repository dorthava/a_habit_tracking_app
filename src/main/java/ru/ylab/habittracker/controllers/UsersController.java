package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.services.UsersService;

public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    public BaseResponse updatingTheUserProfile(User user) {
        return usersService.update(user);
    }

    public void deleteUserByEmail(String email) {
        usersService.delete(email);
    }

    public BaseResponse forgotPassword(String email, String password) {
        return usersService.updatePasswordByEmail(email, password);
    }
}
