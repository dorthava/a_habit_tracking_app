package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.utils.Role;

public interface UsersService {
    User create(User user);
    BaseResponse<User> update(User user);
    void delete(String email);
    BaseResponse<User> updatePasswordByEmail(String email, String newPassword);
    BaseResponse<Void> blockUser(String adminEmail, String email);
    BaseResponse<Void> deleteUser(String adminEmail, String email);
    void setAdminRole(String email);
}
