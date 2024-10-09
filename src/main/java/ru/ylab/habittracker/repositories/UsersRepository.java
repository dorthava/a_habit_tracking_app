package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.User;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
