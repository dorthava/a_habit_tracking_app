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
import ru.ylab.habittracker.exception.HabitNotFoundException;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.repositories.HabitCompletionRepository;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.impl.HabitCompletionRepositoryImpl;
import ru.ylab.habittracker.repositories.impl.HabitsRepositoryImpl;
import ru.ylab.habittracker.services.HabitCompletionService;
import ru.ylab.habittracker.services.impl.HabitCompletionServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HabitCompletionServiceImplTest {
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    private static HabitCompletionService habitCompletionService;
    private static HabitCompletionRepository habitCompletionRepository;
    private static HabitsRepository habitsRepository;

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
            statement.execute("DROP SEQUENCE IF EXISTS habit_tracking_schema.habit_completion_sequence CASCADE");
            statement.execute("DROP TABLE IF EXISTS habit_tracking_schema.habit_completion CASCADE");

            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS habit_tracking_schema.habit_completion_sequence");
            statement.executeUpdate("CREATE TABLE habit_tracking_schema.habit_completion (id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('habit_tracking_schema.habit_completion_sequence')," +
                    "    completion_date DATE NOT NULL," +
                    "    habit_id BIGINT NOT NULL," +
                    "    FOREIGN KEY (habit_id) REFERENCES habit_tracking_schema.habit(id) ON DELETE CASCADE)");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        habitCompletionRepository = new HabitCompletionRepositoryImpl(databaseConnection);
        habitsRepository = new HabitsRepositoryImpl(databaseConnection);
        habitCompletionService = new HabitCompletionServiceImpl(habitsRepository, habitCompletionRepository);
    }

    @Test
    @DisplayName("Given habit not completed today When markCompletion Then complete habit")
    void givenHabitNotCompletedToday_WhenMarkCompletion_ThenCompleteHabit() {
        Long habitId = 1L;
        LocalDate today = LocalDate.now();

        assertTrue(habitCompletionRepository.findByHabitId(habitId).isEmpty(), "Habit should not have any completions yet");

        HabitCompletion habitCompletion = habitCompletionService.markCompletion(habitId);

        assertNotNull(habitCompletion);
        assertEquals(habitId, habitCompletion.getHabitId(), "The habit ID should match");
        assertEquals(today, habitCompletion.getCompletionDate(), "The completion date should be today");

        assertFalse(habitCompletionRepository.findByHabitId(habitId).isEmpty(), "Habit should have at least one completion");
    }

    @Test
    @DisplayName("Given habit with completions When showTheHistory Then return habit completion history")
    void givenHabitWithCompletions_WhenShowTheHistory_ThenReturnHabitCompletionHistory() {
        Long habitId = 1L;

        BaseResponse<List<HabitCompletion>> response = habitCompletionService.showTheHistory(habitId);

        assertNotNull(response);
        assertEquals("The habit history found.", response.status());
        assertNotNull(response.data());
    }

    @Test
    @DisplayName("Given empty completion list When calculateCurrentStreak Then throw HabitNotFoundException")
    void givenEmptyCompletionList_WhenCalculateCurrentStreak_ThenThrowHabitNotFoundException() {
        HabitNotFoundException exception = assertThrows(HabitNotFoundException.class, () -> {
            habitCompletionService.calculateCurrentStreak(2L, LocalDate.now());
        });

        assertEquals("Habit completion list is empty.", exception.getMessage(), "Exception message should match");
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }
}
