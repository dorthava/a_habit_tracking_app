package ru.ylab.habittracker.services;

import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.UsersRepository;

public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void signUp(String email, String password) {
        usersRepository.save(new User(null, email, password));
    }

    @Override
    public void signIn(String email, String password) {
        usersRepository.findByEmail(email).ifPresentOrElse(user -> {
            if (user.getPassword().equals(password)) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Invalid email or password.");
            }
        }, () -> System.out.println("User not found."));
    }
}
