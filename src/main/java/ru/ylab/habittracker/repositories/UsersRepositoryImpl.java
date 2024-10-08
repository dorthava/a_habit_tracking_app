package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.utils.IdsGenerator;

import java.util.HashMap;
import java.util.Optional;

public class UsersRepositoryImpl implements UsersRepository {
    private final HashMap<String, User> dataSource;

    public UsersRepositoryImpl(HashMap<String, User> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(dataSource.get(email));
    }

    @Override
    public void save(User entity) {
        if (findByEmail(entity.getEmail()).isPresent()) {
            System.out.println("User already exists");
            return;
        }

        entity.setId(IdsGenerator.getInstance().generateId());
        dataSource.put(entity.getEmail(), entity);
        System.out.println("Saved user: " + entity);
    }
}
