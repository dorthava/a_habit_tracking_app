package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.UpdateUserProfileRequest;
import ru.ylab.habittracker.dto.UserResponse;
import ru.ylab.habittracker.services.UsersService;

public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    public BaseResponse<UserResponse> updatingTheUserProfile(UpdateUserProfileRequest updateUserProfileRequest) {
        BaseResponse<UserResponse> result;
        try {
            result = usersService.update(updateUserProfileRequest);
        } catch (RuntimeException e) {
            result = new BaseResponse<>(e.getMessage(), null);
        }
        return result;
    }

    public void deleteUserById(Long id) {
        usersService.delete(id);
    }
}
