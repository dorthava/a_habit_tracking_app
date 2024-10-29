package ru.ylab.habittracker.repositories.impl;

import ru.ylab.habittracker.app.DatabaseConnection;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.repositories.HabitCompletionRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория для работы с завершениями привычек.
 */
public class HabitCompletionRepositoryImpl implements HabitCompletionRepository {
    DatabaseConnection databaseConnection;

    public HabitCompletionRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Возвращает список всех завершений привычек.
     *
     * @return список завершений привычек
     */
    @Override
    public List<HabitCompletion> findAll() {
        List<HabitCompletion> habitCompletionList = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.habit_completion")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                LocalDate completionDate = resultSet.getDate("completion_date").toLocalDate();
                Long habitId = resultSet.getLong("habit_id");
                habitCompletionList.add(new HabitCompletion(id, completionDate, habitId));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении всех завершений привычек: " + e.getMessage());
        }
        return habitCompletionList;
    }

    /**
     * Сохраняет новое завершение привычки.
     *
     * @param entity объект завершения привычки для сохранения
     * @return сохраненное завершение привычки, включая его ID
     */
    @Override
    public HabitCompletion save(HabitCompletion entity) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO habit_tracking_schema.habit_completion(completion_date, habit_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(entity.getCompletionDate()));
            statement.setLong(2, entity.getHabitId());
            statement.execute();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    return new HabitCompletion(id, entity.getCompletionDate(), entity.getHabitId());
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении завершения привычки: " + e.getMessage());
        }
        return null;
    }

    /**
     * Обновляет существующее завершение привычки.
     *
     * @param entity объект завершения привычки с обновленными данными
     * @return обновленное завершение привычки, или null, если обновление не удалось
     */
    @Override
    public HabitCompletion update(HabitCompletion entity) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE habit_tracking_schema.habit_completion SET completion_date = ?, habit_id = ? WHERE id = ?")) {
            statement.setDate(1, Date.valueOf(entity.getCompletionDate()));
            statement.setLong(2, entity.getHabitId());
            statement.setLong(3, entity.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении завершения привычки: " + e.getMessage());
        }
        return null;
    }

    /**
     * Удаляет завершение привычки по указанному ID.
     *
     * @param id идентификатор завершения привычки для удаления
     */
    @Override
    public void delete(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM habit_tracking_schema.habit_completion WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении завершения привычки: " + e.getMessage());
        }
    }

    /**
     * Находит завершение привычки по его ID.
     *
     * @param id идентификатор завершения привычки
     * @return объект завершения привычки, если найден, иначе Optional.empty()
     */
    @Override
    public Optional<HabitCompletion> findById(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.habit_completion WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LocalDate completionDate = resultSet.getDate("completion_date").toLocalDate();
                    Long habitId = resultSet.getLong("habit_id");
                    return Optional.of(new HabitCompletion(id, completionDate, habitId));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске завершения привычки по ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Находит завершения привычки по ID привычки.
     *
     * @param habitId идентификатор привычки
     * @return список завершений привычки с указанным ID
     */
    @Override
    public List<HabitCompletion> findByHabitId(Long habitId) {
        List<HabitCompletion> habitCompletionList = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM habit_tracking_schema.habit_completion WHERE habit_id = ?")) {
            statement.setLong(1, habitId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    LocalDate completionDate = resultSet.getDate("completion_date").toLocalDate();
                    habitCompletionList.add(new HabitCompletion(id, completionDate, habitId));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске завершений привычки по ID привычки: " + e.getMessage());
        }
        return habitCompletionList;
    }

    /**
     * Находит завершения привычки по ID привычки и периоду.
     *
     * @param habitId идентификатор привычки
     * @param startDate дата начала периода
     * @param endDate дата окончания периода
     * @return список завершений привычки в заданном периоде
     */
    @Override
    public List<HabitCompletion> findByHabitIdAndPeriod(Long habitId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM habit_tracking_schema.habit_completion WHERE habit_id = ? AND completion_date BETWEEN ? AND ?";
        List<HabitCompletion> habitCompletionList = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, habitId);
            preparedStatement.setDate(2, Date.valueOf(startDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    LocalDate completionDate = rs.getDate("completion_date").toLocalDate();
                    Long periodId = rs.getLong("habit_id");
                    habitCompletionList.add(new HabitCompletion(id, completionDate, periodId));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске завершений привычки по ID и периоду: " + e.getMessage());
        }
        return habitCompletionList;
    }
}
