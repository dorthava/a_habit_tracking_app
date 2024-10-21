package ru.ylab.habbittracker.repositories;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.ylab.habittracker.app.DatabaseConnection;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.repositories.HabitCompletionRepository;
import ru.ylab.habittracker.repositories.impl.HabitCompletionRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HabitCompletionRepositoryImplTest {
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    private static HabitCompletionRepository habitCompletionRepository;

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
    }

    @Test
    @DisplayName("Given existing habit completions When findAll Then return list of completions")
    void givenExistingHabitCompletions_WhenFindAll_ThenReturnListOfCompletions() {
        HabitCompletion habitCompletion = new HabitCompletion(null, LocalDate.now(), 1L);
        HabitCompletion savedCompletion = habitCompletionRepository.save(habitCompletion);
        assertNotNull(savedCompletion);

        List<HabitCompletion> completions = habitCompletionRepository.findAll();

        assertFalse(completions.isEmpty());
    }

    @Test
    @DisplayName("Given no habit completions When findAll Then return empty list")
    void givenNoHabitCompletions_WhenFindAll_ThenReturnEmptyList() {
        List<HabitCompletion> completions = habitCompletionRepository.findAll();

        assertTrue(completions.isEmpty());
    }

    @Test
    @DisplayName("Given a valid habit completion When save Then return saved completion")
    void givenValidHabitCompletion_WhenSave_ThenReturnSavedCompletion() {
        HabitCompletion habitCompletion = new HabitCompletion(null, LocalDate.now(), 1L);

        HabitCompletion savedCompletion = habitCompletionRepository.save(habitCompletion);

        System.out.println(savedCompletion.getId());
        assertNotNull(savedCompletion);
        assertNotNull(savedCompletion.getId());
        assertEquals(habitCompletion.getCompletionDate(), savedCompletion.getCompletionDate());
        assertEquals(habitCompletion.getHabitId(), savedCompletion.getHabitId());
    }

    @Test
    @DisplayName("Given an invalid habit completion When save Then handle SQLException")
    void givenInvalidHabitCompletion_WhenSave_ThenHandleSQLException() {
        HabitCompletion invalidCompletion = new HabitCompletion(null, LocalDate.now(), -1L);

        HabitCompletion result = habitCompletionRepository.save(invalidCompletion);

        assertNull(result);
    }

    @Test
    @DisplayName("Given a valid habit completion When update Then return updated completion")
    void givenValidHabitCompletion_WhenUpdate_ThenReturnUpdatedCompletion() {
        HabitCompletion habitCompletion = new HabitCompletion(null, LocalDate.now(), 1L);
        HabitCompletion savedCompletion = habitCompletionRepository.save(habitCompletion);

        savedCompletion.setCompletionDate(LocalDate.now().plusDays(1));

        HabitCompletion updatedCompletion = habitCompletionRepository.update(savedCompletion);

        assertNotNull(updatedCompletion);
        assertEquals(savedCompletion.getCompletionDate(), updatedCompletion.getCompletionDate());
        assertEquals(savedCompletion.getHabitId(), updatedCompletion.getHabitId());
        assertEquals(savedCompletion.getId(), updatedCompletion.getId());
    }

    @Test
    @DisplayName("Given an invalid habit completion When update Then handle SQLException")
    void givenInvalidHabitCompletion_WhenUpdate_ThenHandleSQLException() {
        HabitCompletion invalidCompletion = new HabitCompletion(-1L, LocalDate.now(), 1L);

        HabitCompletion result = habitCompletionRepository.update(invalidCompletion);

        assertNull(result);
    }

    @Test
    @DisplayName("Given an existing habit completion When delete Then completion is removed")
    void givenExistingHabitCompletion_WhenDelete_ThenCompletionIsRemoved() {
        HabitCompletion habitCompletion = new HabitCompletion(null, LocalDate.now(), 1L);
        HabitCompletion savedCompletion = habitCompletionRepository.save(habitCompletion);

        habitCompletionRepository.delete(savedCompletion.getId());

        Optional<HabitCompletion> result = habitCompletionRepository.findById(savedCompletion.getId());
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Given a non-existing habit completion When delete Then handle SQLException")
    void givenNonExistingHabitCompletion_WhenDelete_ThenHandleSQLException() {
        Long nonExistingId = -1L;

        assertDoesNotThrow(() -> habitCompletionRepository.delete(nonExistingId));
    }

    @Test
    @DisplayName("Given existing HabitCompletion ID, When findById is called, Then it should return the HabitCompletion")
    void testFindById_ExistingId_ShouldReturnHabitCompletion() {
        HabitCompletion habitCompletion = new HabitCompletion(null, LocalDate.now(), 1L);
        habitCompletionRepository.save(habitCompletion);

        Long existingId = 1L;
        LocalDate expectedCompletionDate = LocalDate.now();
        Long expectedHabitId = 1L;

        Optional<HabitCompletion> result = habitCompletionRepository.findById(existingId);

        assertTrue(result.isPresent());
        assertEquals(existingId, result.get().getId());
        assertEquals(expectedCompletionDate, result.get().getCompletionDate());
        assertEquals(expectedHabitId, result.get().getHabitId());
    }

    @Test
    @DisplayName("Given non-existing HabitCompletion ID, When findById is called, Then it should return empty")
    void testFindById_NonExistingId_ShouldReturnEmpty() {
        Long nonExistingId = 999L;

        Optional<HabitCompletion> result = habitCompletionRepository.findById(nonExistingId);

        assertFalse(result.isPresent(), "HabitCompletion should not be present");
    }

    @Test
    @DisplayName("Given existing Habit ID, When findByHabitId is called, Then it should return list of HabitCompletions")
    void testFindByHabitId_ExistingHabitId_ShouldReturnHabitCompletions() {
        Long existingHabitId = 1L;
        HabitCompletion habitCompletion = new HabitCompletion(null, LocalDate.now(), existingHabitId);
        habitCompletionRepository.save(habitCompletion);

        List<HabitCompletion> result = habitCompletionRepository.findByHabitId(existingHabitId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Given non-existing Habit ID, When findByHabitId is called, Then it should return empty list")
    void testFindByHabitId_NonExistingHabitId_ShouldReturnEmptyList() {
        Long nonExistingHabitId = 999L;

        List<HabitCompletion> result = habitCompletionRepository.findByHabitId(nonExistingHabitId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given existing Habit ID and period, When findByHabitIdAndPeriod is called, Then it should return list of HabitCompletions")
    void testFindByHabitIdAndPeriod_ExistingHabitIdAndPeriod_ShouldReturnHabitCompletions() {
        Long existingHabitId = 1L;
        HabitCompletion saveHabitCompletion = new HabitCompletion(null, LocalDate.now(), existingHabitId);
        habitCompletionRepository.save(saveHabitCompletion);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();

        List<HabitCompletion> result = habitCompletionRepository.findByHabitIdAndPeriod(existingHabitId, startDate, endDate);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(habitCompletion ->
                        !habitCompletion.getCompletionDate().isBefore(startDate) &&
                                !habitCompletion.getCompletionDate().isAfter(endDate)));
    }

    @Test
    @DisplayName("Given non-existing Habit ID, When findByHabitIdAndPeriod is called, Then it should return empty list")
    void testFindByHabitIdAndPeriod_NonExistingHabitId_ShouldReturnEmptyList() {
        Long nonExistingHabitId = 999L;

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();

        List<HabitCompletion> result = habitCompletionRepository.findByHabitIdAndPeriod(nonExistingHabitId, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given existing Habit ID with no completions in period, When findByHabitIdAndPeriod is called, Then it should return empty list")
    void testFindByHabitIdAndPeriod_ExistingHabitIdWithNoCompletionsInPeriod_ShouldReturnEmptyList() {
        Long existingHabitId = 1L;
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 10);

        List<HabitCompletion> result = habitCompletionRepository.findByHabitIdAndPeriod(existingHabitId, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }
}
