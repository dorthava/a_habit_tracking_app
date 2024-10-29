package ru.ylab.habittracker.repositories.impl;

import ru.ylab.habittracker.app.DatabaseConnection;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.utils.Frequency;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Реализация репозитория для работы с привычками.
 * Этот класс предоставляет методы для выполнения CRUD операций над сущностями Habit в базе данных.
 */
public class HabitsRepositoryImpl implements HabitsRepository {
    DatabaseConnection databaseConnection;

    public HabitsRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Получает все привычки из базы данных.
     *
     * @return Список всех привычек.
     */
    @Override
    public List<Habit> findAll() {
        List<Habit> habits = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.habit")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Frequency frequency = Frequency.fromValue(resultSet.getInt("frequency"));
                Long userId = resultSet.getLong("user_id");
                LocalDate createdDate = resultSet.getDate("created_date").toLocalDate();
                Habit habit = new Habit(id, name, description, frequency, userId, createdDate);
                habits.add(habit);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching habits: " + e.getMessage());
        }
        return habits;
    }

    /**
     * Сохраняет новую привычку в базе данных.
     *
     * @param entity Привычка для сохранения.
     * @return Сохраненная привычка с назначенным идентификатором, или null, если сохранение не удалось.
     */
    @Override
    public Habit save(Habit entity) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO habit_tracking_schema.habit(name, description, frequency, created_date, user_id) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setInt(3, entity.getFrequency().ordinal());
            statement.setDate(4, Date.valueOf(entity.getCreatedDate()));
            statement.setLong(5, entity.getUserId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        return new Habit(id, entity.getName(), entity.getDescription(), entity.getFrequency(),
                                entity.getUserId(), entity.getCreatedDate());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving habit: " + e.getMessage());
        }
        return null;
    }

    /**
     * Обновляет существующую привычку в базе данных.
     *
     * @param entity Привычка с обновленными данными.
     * @return Обновленная привычка, или null, если обновление не удалось.
     */
    @Override
    public Habit update(Habit entity) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE habit_tracking_schema.habit SET name = ?, description = ?, frequency = ?, user_id = ?, created_date = ? WHERE id = ?")) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setInt(3, entity.getFrequency().ordinal());
            statement.setLong(4, entity.getUserId());
            statement.setDate(5, Date.valueOf(entity.getCreatedDate()));
            statement.setLong(6, entity.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        } catch (SQLException e) {
            System.out.println("Error updating habit: " + e.getMessage());
        }
        return null;
    }

    /**
     * Ищет привычку по идентификатору.
     *
     * @param id Идентификатор привычки.
     * @return Объект Habit, если привычка найдена, иначе Optional.empty().
     */
    @Override
    public Optional<Habit> findById(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.habit WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Frequency frequency = Frequency.fromValue(resultSet.getInt("frequency"));
                Long userId = resultSet.getLong("user_id");
                LocalDate createdDate = resultSet.getDate("created_date").toLocalDate();

                return Optional.of(new Habit(id, name, description, frequency, userId, createdDate));
            }
        } catch (SQLException e) {
            System.out.println("Error finding habit by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Ищет все привычки, принадлежащие определенному пользователю.
     *
     * @param userId Идентификатор пользователя.
     * @return Список привычек, принадлежащих пользователю.
     */
    @Override
    public List<Habit> findByUserId(Long userId) {
        List<Habit> habits = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.habit WHERE user_id = ?")) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Frequency frequency = Frequency.fromValue(resultSet.getInt("frequency"));
                LocalDate createdDate = resultSet.getDate("created_date").toLocalDate();
                Habit habit = new Habit(id, name, description, frequency, userId, createdDate);
                habits.add(habit);
            }
        } catch (SQLException e) {
            System.out.println("Error finding habits by user ID: " + e.getMessage());
        }
        return habits;
    }

    /**
     * Ищет привычки, принадлежащие пользователю и созданные в определенную дату.
     *
     * @param userId      Идентификатор пользователя.
     * @param createdDate Дата создания привычки.
     * @return Список привычек, соответствующих критериям поиска.
     */
    @Override
    public List<Habit> findByUserIdAndDate(Long userId, LocalDate createdDate) {
        List<Habit> habits = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.habit WHERE user_id = ? AND created_date = ?")) {
            statement.setLong(1, userId);
            statement.setDate(2, Date.valueOf(createdDate));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Frequency frequency = Frequency.fromValue(resultSet.getInt("frequency"));
                Habit habit = new Habit(id, name, description, frequency, userId, createdDate);
                habits.add(habit);
            }
        } catch (SQLException e) {
            System.out.println("Error finding habits by user ID and date: " + e.getMessage());
        }
        return habits;
    }

    /**
     * Удаляет привычку по идентификатору.
     *
     * @param id Идентификатор привычки, которую нужно удалить.
     */
    @Override
    public void delete(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM habit_tracking_schema.habit WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting habit: " + e.getMessage());
        }
    }
}
