package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.services.UsersService;

public class AdministrationController {
    private final UsersService usersService;

    public AdministrationController(UsersService usersService) {
        this.usersService = usersService;
    }

    public BaseResponse<Void> blockUser(String adminEmail, String email) {
        return usersService.blockUser(adminEmail, email);
    }

    public BaseResponse<Void> deleteUser(String adminEmail, String email) {
        return usersService.deleteUser(adminEmail, email);
    }

    public void setAdminRole(String email) {
        usersService.setAdminRole(email);
    }
}
