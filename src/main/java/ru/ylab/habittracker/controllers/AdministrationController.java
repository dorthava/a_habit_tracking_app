package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.services.UsersService;

public class AdministrationController {
    private final UsersService usersService;

    public AdministrationController(UsersService usersService) {
        this.usersService = usersService;
    }

    public BaseResponse<Void> blockUser(Long adminId, Long userId) {
        try {
            return usersService.blockUser(adminId, userId);
        } catch (Exception e) {
            return new BaseResponse<>(e.getMessage(), null);
        }
    }

    public BaseResponse<Void> deleteUser(Long adminId, Long userId) {
        try {
            return usersService.deleteUserByAdmin(adminId, userId);
        } catch (Exception e) {
            return new BaseResponse<>(e.getMessage(), null);
        }
    }

    public void setAdminRole(String email) {
        usersService.setAdminRole(email);
    }
}
