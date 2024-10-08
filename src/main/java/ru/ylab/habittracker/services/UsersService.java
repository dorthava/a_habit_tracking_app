package ru.ylab.habittracker.services;

public interface UsersService {
    void signUp(String email, String password);
    void signIn(String email, String password);
}
