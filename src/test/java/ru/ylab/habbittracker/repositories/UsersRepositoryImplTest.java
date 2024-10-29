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
import ru.ylab.habittracker.models.Users;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.repositories.impl.UsersRepositoryImpl;
import ru.ylab.habittracker.utils.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsersRepositoryImplTest {
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    private static UsersRepository usersRepository;

    @BeforeAll
    public static void beforeAll() throws LiquibaseException, SQLException {
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
            statement.execute("DROP SEQUENCE IF EXISTS habit_tracking_schema.users_sequence CASCADE");
            statement.execute("DROP TABLE IF EXISTS habit_tracking_schema.users CASCADE");

            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS habit_tracking_schema.users_sequence");
            statement.executeUpdate("CREATE TABLE habit_tracking_schema.users ("
                    + "id BIGINT PRIMARY KEY DEFAULT nextval('habit_tracking_schema.users_sequence'), " +
                    "    name VARCHAR(64) NOT NULL UNIQUE, " +
                    "    email VARCHAR(64) NOT NULL UNIQUE, " +
                    "    password VARCHAR(64) NOT NULL, " +
                    "    role INT NOT NULL," +
                    "    is_blocked BOOLEAN NOT NULL)");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        usersRepository = new UsersRepositoryImpl(databaseConnection);
    }

    @Test
    void givenExistingUser_WhenFindByEMail_ThenUserIsReturned() {
        Users user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);

        Optional<Users> foundUser = usersRepository.findByEmail("test1@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("testuser1", foundUser.get().getName());
    }

    @Test
    void givenExistingUser_WhenFindByEmail_ThenEmptyIsReturned() {
        Optional<Users> foundUser = usersRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void givenExistingUser_WhenFindById_ThenUserIsReturned() {
        Users user = new Users(null, "testuser2", "test2@example.com", "password", Role.USER, false);
        usersRepository.save(user);

        Optional<Users> foundUser = usersRepository.findById(1L);
        assertTrue(foundUser.isPresent());
        assertEquals("testuser2", foundUser.get().getName());
    }

    @Test
    void givenNonExistingUser_WhenFindById_ThenEmptyIsReturned() {
        Optional<Users> foundUser = usersRepository.findById(999L);
        assertFalse(foundUser.isPresent());
    }

    @Test
    void givenNoUsers_WhenFindAll_ThenEmptyListIsReturned() {
        List<Users> users = usersRepository.findAll();
        assertTrue(users.isEmpty(), "Expected an empty list of users");
    }

    @Test
    void givenMultipleUsers_WhenFindAll_ThenAllUsersAreReturned() {
        Users user1 = new Users(null, "User One", "user1@example.com", "password1", Role.USER, false);
        Users user2 = new Users(null, "User Two", "user2@example.com", "password2", Role.USER, false);
        usersRepository.save(user1);
        usersRepository.save(user2);

        List<Users> users = usersRepository.findAll();
        assertEquals(2, users.size(), "Expected two users to be returned");
        assertTrue(users.stream().anyMatch(u -> "User One".equals(u.getName())), "Expected User One to be in the list");
        assertTrue(users.stream().anyMatch(u -> "User Two".equals(u.getName())), "Expected User Two to be in the list");
    }

    @Test
    void givenValidUser_WhenSaved_ThenUserIsReturnedWithId() {
        Users user = new Users(null, "Valid User", "valid@example.com", "password", Role.USER, false);

        Users savedUser = usersRepository.save(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("Valid User", savedUser.getName());
        assertEquals("valid@example.com", savedUser.getEmail());
    }

    @Test
    void givenUserWithExistingEmail_WhenSaved_ThenReturnNull() {
        Users user1 = new Users(null, "User1", "duplicate@example.com", "password1", Role.USER, false);
        usersRepository.save(user1);

        Users user2 = new Users(null, "User2", "duplicate@example.com", "password2", Role.USER, false);
        Users savedUser = usersRepository.save(user2);

        assertNull(savedUser);
    }

    @Test
    void givenExistingUser_WhenUpdated_ThenUserIsUpdated() {
        Users user = new Users(null, "Original User", "original@example.com", "password", Role.USER, false);
        Users savedUser = usersRepository.save(user);

        savedUser.setName("Updated User");
        savedUser.setEmail("updated@example.com");
        savedUser.setPassword("newpassword");
        savedUser.setBlocked(true);

        Users updatedUser = usersRepository.update(savedUser);

        assertNotNull(updatedUser);
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Updated User", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertTrue(updatedUser.isBlocked());
    }

    @Test
    void givenNonExistingUser_WhenUpdated_ThenReturnNull() {
        Users user = new Users(999L, "Non-Existing User", "nonexisting@example.com", "password", Role.USER, false);
        Users updatedUser = usersRepository.update(user);
        assertNull(updatedUser);
    }

    @Test
    void givenExistingUser_WhenDeleted_ThenUserDoesNotExist() {
        Users user = new Users(null, "User to Delete", "delete@example.com", "password", Role.USER, false);
        Users savedUser = usersRepository.save(user);

        usersRepository.delete(savedUser.getId());

        Optional<Users> foundUser = usersRepository.findById(savedUser.getId());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void givenNonExistingUser_WhenDeleted_ThenNoExceptionIsThrown() {
        assertDoesNotThrow(() -> usersRepository.delete(999L));
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }
}