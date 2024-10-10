package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.UsersRepository;

import java.util.Optional;

public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public User create(User user) {
        return usersRepository.save(user);
    }

    @Override
    public BaseResponse update(User user) {
        Optional<User> optionalUser = usersRepository.findByEmail(user.getEmail());
        if(optionalUser.isEmpty()) {
            return new BaseResponse(false, "User not found");
        }
        usersRepository.save(user);
        return new BaseResponse(true, "User updated");
    }

    @Override
    public void delete(String email) {
        usersRepository.delete(email);
    }

    @Override
    public BaseResponse updatePasswordByEmail(String email, String newPassword) {
        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return new BaseResponse(false, "User not found");
        }
        User user = optionalUser.get();
        user.setPassword(newPassword);
        return new BaseResponse(true, "Password updated");
    }
}
