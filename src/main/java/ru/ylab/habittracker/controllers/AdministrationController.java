package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.services.UsersService;
import ru.ylab.habittracker.utils.Role;

public class AdministrationController {
    private final UsersService usersService;

    public AdministrationController(UsersService usersService) {
        this.usersService = usersService;
    }

    public BaseResponse<Void> blockUser(Role role, String email) {
        return usersService.blockUser(role, email);
    }

    public BaseResponse<Void> deleteUser(Role role, String email) {
        return usersService.deleteUser(role, email);
    }

    public void setAdminRole(String email) {
        usersService.setAdminRole(email);
    }
}
