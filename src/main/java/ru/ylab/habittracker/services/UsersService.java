package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.User;

public interface UsersService {
    User create(User user);
    BaseResponse update(User user);
    void delete(String email);
    BaseResponse updatePasswordByEmail(String email, String newPassword);
}
