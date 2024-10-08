package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.services.UsersService;

public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    public void signIn(String username, String password) {
        usersService.signIn(username, password);
    }

    public void signUp(String username, String password) {
        usersService.signUp(username, password);
    }
}
