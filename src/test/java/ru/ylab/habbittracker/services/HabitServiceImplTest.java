package ru.ylab.habbittracker.services;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.ylab.habittracker.app.DatabaseConnection;
import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.impl.HabitsRepositoryImpl;
import ru.ylab.habittracker.repositories.impl.UsersRepositoryImpl;
import ru.ylab.habittracker.services.HabitsService;
import ru.ylab.habittracker.services.impl.HabitsServiceImpl;
import ru.ylab.habittracker.utils.Frequency;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HabitServiceImplTest {
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    private static HabitsRepository habitsRepository;
    private static HabitsService habitsService;

    @BeforeAll
    static void beforeAll() throws LiquibaseException, SQLException {
        postgres.start();
        Connection connection = new DatabaseConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()).getConnection();
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update();
    }

    @BeforeEach
    void setUp() {
        DatabaseConnection databaseConnection = new DatabaseConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP SEQUENCE IF EXISTS habit_tracking_schema.habit_sequence CASCADE");
            statement.execute("DROP TABLE IF EXISTS habit_tracking_schema.habit CASCADE");

            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS habit_tracking_schema.habit_sequence");
            statement.executeUpdate("CREATE TABLE habit_tracking_schema.habit ("
                    + "id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('habit_tracking_schema.habit_sequence'),  -- Используем последовательность для генерации id\n" +
                    "    name VARCHAR(64) NOT NULL,\n" +
                    "    description TEXT,\n" +
                    "    frequency INT NOT NULL,\n" +
                    "    created_date DATE NOT NULL,\n" +
                    "    user_id BIGINT NOT NULL,\n" +
                    "    CONSTRAINT fk_habit_user FOREIGN KEY (user_id) REFERENCES habit_tracking_schema.users(id) ON DELETE CASCADE)");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        habitsRepository = new HabitsRepositoryImpl(databaseConnection);
        UsersRepositoryImpl usersRepository = new UsersRepositoryImpl(databaseConnection);
        habitsService = new HabitsServiceImpl(usersRepository, habitsRepository);
    }

    @Test
    @DisplayName("Given valid habit When created Then habit is successfully created")
    void givenValidHabit_WhenCreated_ThenHabitIsSuccessfullyCreated() {
        Habit habit = new Habit(null, "Exercise",
                "Daily exercise", Frequency.DAILY, 1L, LocalDate.now());

        BaseResponse<Habit> response = habitsService.create(habit);

        assertEquals("The habit was successfully created.", response.status());
        assertNotNull(response.data());
        assertEquals("Exercise", response.data().getName());
        assertEquals(1L, response.data().getId());
    }

    @Test
    @DisplayName("Given non-existing user When creating habit Then throw exception")
    void givenNonExistingUser_WhenCreatingHabit_ThenThrowException() {
        Habit habit = new Habit(null, "Reading", "Read 10 pages", Frequency.DAILY,
                99L, LocalDate.now());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habitsService.create(habit);
        });
        assertEquals("The user was not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Given existing habit When updated Then habit is successfully updated")
    void givenExistingHabit_WhenUpdated_ThenHabitIsSuccessfullyUpdated() {
        Habit habit = new Habit(null, "Exercise",
                "Daily exercise", Frequency.DAILY, 1L, LocalDate.now());

        habitsService.create(habit);
        Habit updatedHabit = new Habit(1L, "Exercise", "Updated description", Frequency.DAILY, 1L, LocalDate.now());

        BaseResponse<Habit> response = habitsService.update(updatedHabit);

        assertEquals("The habit was successfully updated.", response.status());
        assertNotNull(response.data());
        assertEquals("Updated description", response.data().getDescription());
    }

    @Test
    @DisplayName("Given non-existing habit When updated Then throw exception")
    void givenNonExistingHabit_WhenUpdated_ThenThrowException() {
        Habit nonExistingHabit = new Habit(99L, "Reading", "Read 10 pages", Frequency.DAILY, 1L, LocalDate.now());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habitsService.update(nonExistingHabit);
        });
        assertEquals("The habit was not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Given non-existing user When updating habit Then throw exception")
    void givenNonExistingUser_WhenUpdatingHabit_ThenThrowException() {
        Habit habitWithNonExistingUser = new Habit(1L, "Exercise", "Daily exercise", Frequency.DAILY, 99L, LocalDate.now());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habitsService.update(habitWithNonExistingUser);
        });
        assertEquals("The user was not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Given existing habit When deleted Then habit is successfully deleted")
    void givenExistingHabit_WhenDeleted_ThenHabitIsSuccessfullyDeleted() {
        Long habitId = 1L;
        Habit habit = new Habit(null, "Exercise",
                "Daily exercise", Frequency.DAILY, 1L, LocalDate.now());
        habitsService.create(habit);
        habitsService.delete(habitId);

        Optional<Habit> deletedHabit = habitsRepository.findById(habitId);
        assertTrue(deletedHabit.isEmpty());
    }

    @Test
    @DisplayName("Given non-existing habit When deleted Then no action is performed")
    void givenNonExistingHabit_WhenDeleted_ThenNoActionIsPerformed() {
        Long nonExistingHabitId = 99L;
        habitsService.delete(nonExistingHabitId);

        Optional<Habit> habit = habitsRepository.findById(nonExistingHabitId);
        assertTrue(habit.isEmpty());
    }

    @Test
    @DisplayName("Given user with habits When findByUserId Then return list of habits")
    void givenUserWithHabits_WhenFindByUserId_ThenReturnListOfHabits() {
        Long userId = 1L;
        Habit habit = new Habit(null, "Exercise",
                "Daily exercise", Frequency.DAILY, 1L, LocalDate.now());

        habitsService.create(habit);
        habit = new Habit(null, "Exercise",
                "Daily exercise", Frequency.DAILY, 1L, LocalDate.now());

        habitsService.create(habit);
        BaseResponse<List<Habit>> response = habitsService.findByUserId(userId);

        assertNotNull(response);
        assertEquals("The habits found.", response.status());
        assertNotNull(response.data());
        assertEquals(2, response.data().size());
    }

    @Test
    @DisplayName("Given user with habits on specific date When findByUserIdAndDate Then return habits for that date")
    void givenUserWithHabitsOnSpecificDate_WhenFindByUserIdAndDate_ThenReturnHabitsForThatDate() {
        Long userId = 1L;
        LocalDate targetDate = LocalDate.of(2024, 10, 21);
        Habit habit = new Habit(null, "Exercise",
                "Daily exercise", Frequency.DAILY, 1L, targetDate);
        habitsService.create(habit);
        BaseResponse<List<Habit>> response = habitsService.findByUserIdAndDate(userId, targetDate);

        assertNotNull(response);
        assertEquals("The habits found.", response.status());
        assertNotNull(response.data());
        assertEquals(1, response.data().size());
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }
}
