package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.utils.Role;

import java.util.Optional;

public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final HabitsRepository habitsRepository;

    public UsersServiceImpl(UsersRepository usersRepository, HabitsRepository habitsRepository) {
        this.usersRepository = usersRepository;
        this.habitsRepository = habitsRepository;
    }

    @Override
    public User create(User user) {
        return usersRepository.save(user);
    }

    @Override
    public BaseResponse<User> update(User user) {
        Optional<User> optionalUser = usersRepository.findByEmail(user.getEmail());
        if(optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "User not found", null);
        }
        user = usersRepository.save(user);
        return new BaseResponse<>(true, "User updated", user);
    }

    @Override
    public void delete(String email) {
        usersRepository.delete(email);
        habitsRepository.delete(email);
    }

    @Override
    public BaseResponse<User> updatePasswordByEmail(String email, String newPassword) {
        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "User not found", null);
        }
        User user = optionalUser.get();
        user.setPassword(newPassword);
        return new BaseResponse<>(true, "Password updated", user);
    }

    @Override
    public BaseResponse<Void> blockUser(String adminEmail, String email) {
        Optional<User> optionalAdmin = usersRepository.findByEmail(adminEmail);
        if(optionalAdmin.isEmpty()) {
            return new BaseResponse<>(false, "Admin not found", null);
        }

        if(optionalAdmin.get().getRole() == Role.USER) {
            return new BaseResponse<>(false, "Forbidden", null);
        }

        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "User not found", null);
        }

        User user = optionalUser.get();
        user.setBlocked(true);
        return new BaseResponse<>(true, "User blocked", null);
    }

    @Override
    public BaseResponse<Void> deleteUser(String adminEmail, String email) {
        Optional<User> optionalAdmin = usersRepository.findByEmail(adminEmail);
        if(optionalAdmin.isEmpty()) {
            return new BaseResponse<>(false, "Admin not found", null);
        }

        if(optionalAdmin.get().getRole() == Role.USER) {
            return new BaseResponse<>(false, "Forbidden", null);
        }
        usersRepository.delete(email);
        return new BaseResponse<>(true, "User deleted", null);
    }

    @Override
    public void setAdminRole(String email) {
        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        user.setRole(Role.ADMIN);
    }

}
