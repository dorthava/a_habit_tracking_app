package ru.ylab.habittracker.repositories.impl;

import ru.ylab.habittracker.app.DatabaseConnection;
import ru.ylab.habittracker.models.Users;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.utils.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория для работы с пользователями.
 * Этот класс предоставляет методы для выполнения CRUD операций над сущностями Users в базе данных.
 */
public class UsersRepositoryImpl implements UsersRepository {
    DatabaseConnection databaseConnection;

    public UsersRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Находит пользователя по его адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя.
     * @return Optional<Users> объект Users, если найден, иначе пустой Optional.
     */
    @Override
    public Optional<Users> findByEmail(String email) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRowToUser(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return Optional<Users> объект Users, если найден, иначе пустой Optional.
     */
    @Override
    public Optional<Users> findById(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.users WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRowToUser(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Находит всех пользователей в базе данных.
     *
     * @return список всех пользователей.
     */
    @Override
    public List<Users> findAll() {
        List<Users> users = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapRowToUser(resultSet).orElse(null));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    /**
     * Сохраняет нового пользователя в базе данных.
     *
     * @param entity объект Users для сохранения.
     * @return Users объект со сгенерированным идентификатором, если сохранение успешно, иначе null.
     */
    @Override
    public Users save(Users entity) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO habit_tracking_schema.users(name, email, password, role, is_blocked) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getEmail());
            statement.setString(3, entity.getPassword());
            statement.setInt(4, entity.getRole().ordinal());
            statement.setBoolean(5, entity.isBlocked());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    return new Users(id, entity.getName(), entity.getEmail(), entity.getPassword(), entity.getRole(), entity.isBlocked());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Обновляет информацию о существующем пользователе.
     *
     * @param entity объект Users с обновленными данными.
     * @return Users объект, если обновление успешно, иначе null.
     */
    @Override
    public Users update(Users entity) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE habit_tracking_schema.users SET name = ?, email = ?, password = ?, role = ?, is_blocked = ? WHERE id = ?")) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getEmail());
            statement.setString(3, entity.getPassword());
            statement.setInt(4, entity.getRole().ordinal());
            statement.setBoolean(5, entity.isBlocked());
            statement.setLong(6, entity.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return new Users(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getRole(), entity.isBlocked());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно удалить.
     */
    @Override
    public void delete(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM habit_tracking_schema.users WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Преобразует строку результата из ResultSet в объект Users.
     *
     * @param resultSet Результат запроса к базе данных.
     * @return Optional<Users> содержащий объект Users, если он найден, иначе пустой Optional.
     * @throws SQLException если возникают ошибки при извлечении данных.
     */
    private Optional<Users> mapRowToUser(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        Role role = Role.fromValue(resultSet.getInt("role"));
        boolean isBlocked = resultSet.getBoolean("is_blocked");
        return Optional.of(new Users(id, name, email, password, role, isBlocked));
    }
}
