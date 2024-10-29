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
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.impl.HabitsRepositoryImpl;
import ru.ylab.habittracker.utils.Frequency;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HabitRepositoryImplTest {
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

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
    }

    @Test
    void givenNoHabits_WhenFindAll_ThenReturnEmptyList() {
        List<Habit> habits = habitsRepository.findAll();

        assertEquals(0, habits.size());
    }

    @Test
    void givenExistingHabits_WhenFindAll_ThenReturnAllHabits() {
        habitsRepository.save(new Habit(null,"Habit 1", "Description 1", Frequency.DAILY, 1L, LocalDate.now()));
        habitsRepository.save(new Habit(null,"Habit 2", "Description 2", Frequency.DAILY, 1L, LocalDate.now()));

        List<Habit> habits = habitsRepository.findAll();

        assertEquals(2, habits.size());
        assertEquals("Habit 1", habits.get(0).getName());
        assertEquals("Description 1", habits.get(0).getDescription());
        assertEquals("Habit 2", habits.get(1).getName());
        assertEquals("Description 2", habits.get(1).getDescription());
    }

    @Test
    public void givenValidHabit_WhenSaved_ThenHabitIsPersisted() {
        Habit habit = new Habit(null, "Test Habit", "Test Description", Frequency.DAILY, 1L, LocalDate.now());

        Habit savedHabit = habitsRepository.save(habit);

        assertNotNull(savedHabit);
        assertNotNull(savedHabit.getId());
        assertEquals(habit.getName(), savedHabit.getName());
        assertEquals(habit.getDescription(), savedHabit.getDescription());
        assertEquals(habit.getFrequency(), savedHabit.getFrequency());
        assertEquals(habit.getUserId(), savedHabit.getUserId());
    }

    @Test
    public void givenInvalidHabit_WhenSaved_ThenReturnNull() {
        Habit invalidHabit = new Habit(null, null, "Test Description", Frequency.DAILY, 1L, LocalDate.now());

        Habit savedHabit = habitsRepository.save(invalidHabit);

        assertNull(savedHabit);
    }

    @Test
    @DisplayName("Given existing habit When updated Then habit is updated")
    void givenExistingHabit_WhenUpdated_ThenHabitIsUpdated() {
        Habit originalHabit = new Habit(null, "Test Habit", "Description", Frequency.DAILY, 1L, LocalDate.now());
        Habit savedHabit = habitsRepository.save(originalHabit);

        savedHabit.setName("Updated Habit Name");
        savedHabit.setDescription("Updated Description");
        Habit updatedHabit = habitsRepository.update(savedHabit);

        assertNotNull(updatedHabit);
        assertEquals("Updated Habit Name", updatedHabit.getName());
        assertEquals("Updated Description", updatedHabit.getDescription());
    }

    @Test
    @DisplayName("Given non-existing habit When updated Then return null")
    void givenNonExistingHabit_WhenUpdated_ThenReturnNull() {
        Habit nonExistingHabit = new Habit(999L, "Non-existing Habit", "Description", Frequency.WEEKLY, 1L, LocalDate.now());

        Habit result = habitsRepository.update(nonExistingHabit);

        assertNull(result);
    }

    @Test
    @DisplayName("Given existing habit ID When found by ID Then return habit")
    void givenExistingHabitId_WhenFoundById_ThenReturnHabit() {
        Habit habit = new Habit(null, "Test Habit", "Test Description", Frequency.DAILY, 1L, LocalDate.now());
        Habit savedHabit = habitsRepository.save(habit);

        Optional<Habit> foundHabit = habitsRepository.findById(savedHabit.getId());

        assertTrue(foundHabit.isPresent());
        assertEquals(savedHabit.getId(), foundHabit.get().getId());
        assertEquals(savedHabit.getName(), foundHabit.get().getName());
    }

    @Test
    @DisplayName("Given non-existing habit ID When found by ID Then return empty")
    void givenNonExistingHabitId_WhenFoundById_ThenReturnEmpty() {
        Optional<Habit> foundHabit = habitsRepository.findById(999L);

        assertFalse(foundHabit.isPresent());
    }

    @Test
    @DisplayName("Given existing user ID When find habits by user ID Then return list of habits")
    void givenExistingUserId_WhenFindHabitsByUserId_ThenReturnListOfHabits() {
        Long userId = 1L;
        Habit habit1 = new Habit(null, "Test Habit 1", "Test Description 1", Frequency.DAILY, userId, LocalDate.now());
        Habit habit2 = new Habit(null, "Test Habit 2", "Test Description 2", Frequency.WEEKLY, userId, LocalDate.now());
        habitsRepository.save(habit1);
        habitsRepository.save(habit2);

        List<Habit> foundHabits = habitsRepository.findByUserId(userId);

        assertEquals(2, foundHabits.size());
        assertTrue(foundHabits.stream().anyMatch(h -> h.getName().equals("Test Habit 1")));
        assertTrue(foundHabits.stream().anyMatch(h -> h.getName().equals("Test Habit 2")));
    }

    @Test
    @DisplayName("Given non-existing user ID When find habits by user ID Then return empty list")
    void givenNonExistingUserId_WhenFindHabitsByUserId_ThenReturnEmptyList() {
        List<Habit> foundHabits = habitsRepository.findByUserId(999L);

        assertTrue(foundHabits.isEmpty());
    }

    @Test
    @DisplayName("Given existing user ID and date When find habits by user ID and date Then return list of habits")
    void givenExistingUserIdAndDate_WhenFindHabitsByUserIdAndDate_ThenReturnListOfHabits() {
        Long userId = 1L;
        LocalDate createdDate = LocalDate.of(2024, 10, 21);
        Habit habit1 = new Habit(null, "Habit 1", "Description 1", Frequency.DAILY, userId, createdDate);
        Habit habit2 = new Habit(null, "Habit 2", "Description 2", Frequency.WEEKLY, userId, createdDate);
        habitsRepository.save(habit1);
        habitsRepository.save(habit2);

        List<Habit> foundHabits = habitsRepository.findByUserIdAndDate(userId, createdDate);

        assertEquals(2, foundHabits.size());
        assertTrue(foundHabits.stream().anyMatch(h -> h.getName().equals("Habit 1")));
        assertTrue(foundHabits.stream().anyMatch(h -> h.getName().equals("Habit 2")));
    }

    @Test
    @DisplayName("Given non-existing user ID or date When find habits by user ID and date Then return empty list")
    void givenNonExistingUserIdOrDate_WhenFindHabitsByUserIdAndDate_ThenReturnEmptyList() {
        List<Habit> foundHabits = habitsRepository.findByUserIdAndDate(999L, LocalDate.of(2024, 10, 21));

        assertTrue(foundHabits.isEmpty());
    }

    @Test
    @DisplayName("Given existing habit ID When delete habit Then habit is removed from database")
    void givenExistingHabitId_WhenDeleteHabit_ThenHabitIsRemovedFromDatabase() {
        Habit habit = new Habit(null, "Habit to delete", "Description", Frequency.DAILY, 1L, LocalDate.now());
        Habit savedHabit = habitsRepository.save(habit);
        assertNotNull(savedHabit);

        habitsRepository.delete(savedHabit.getId());

        Optional<Habit> deletedHabit = habitsRepository.findById(savedHabit.getId());
        assertTrue(deletedHabit.isEmpty());
    }

    @Test
    @DisplayName("Given non-existing habit ID When delete habit Then no error occurs")
    void givenNonExistingHabitId_WhenDeleteHabit_ThenNoErrorOccurs() {
        assertDoesNotThrow(() -> habitsRepository.delete(999L));
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }
}
