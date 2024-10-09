package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.utils.IdsGenerator;

import java.util.HashMap;
import java.util.List;
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
    public boolean existsByEmail(String email) {
        return dataSource.containsKey(email);
    }

    @Override
    public List<User> findAll() {
        return dataSource.values().stream().toList();
    }

    @Override
    public User save(User entity) {
        Long generatedId = IdsGenerator.getInstance().generateId();
        entity.setId(generatedId);
        dataSource.put(entity.getEmail(), entity);
        return entity;
    }

    @Override
    public User update(User entity) {
        User user = dataSource.get(entity.getEmail());
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        return user;
    }

    @Override
    public void delete(String email) {
        dataSource.remove(email);
    }
}
