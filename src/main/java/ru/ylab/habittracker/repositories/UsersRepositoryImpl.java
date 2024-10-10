package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.utils.IdsGenerator;
import ru.ylab.habittracker.utils.StringValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class UsersRepositoryImpl implements UsersRepository {
    private final HashMap<String, User> dataSource;

    public UsersRepositoryImpl() {
        this.dataSource = new HashMap<>();
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
        if (StringValidator.isValidString(entity.getName())) {
            user.setName(entity.getName());
        }
        if (StringValidator.isValidString(entity.getEmail())) {
            user.setEmail(entity.getEmail());
        }
        if (StringValidator.isValidString(entity.getPassword())) {
            user.setPassword(entity.getPassword());
        }
        return user;
    }

    @Override
    public void delete(String email) {
        dataSource.remove(email);
    }
}
