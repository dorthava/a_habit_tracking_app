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
import ru.ylab.habittracker.dto.*;
import ru.ylab.habittracker.models.Users;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.repositories.impl.UsersRepositoryImpl;
import ru.ylab.habittracker.services.UsersService;
import ru.ylab.habittracker.services.impl.AuthenticationService;
import ru.ylab.habittracker.services.impl.UsersServiceImpl;
import ru.ylab.habittracker.utils.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UsersServiceImplTest {
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");
    private static UsersRepository usersRepository;
    private static UsersService usersService;
    private static AuthenticationService authenticationService;

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
        usersService = new UsersServiceImpl(usersRepository);
        authenticationService = new AuthenticationService(usersService, usersRepository);
    }

    @Test
    @DisplayName("Given existing user When updated Then user is updated")
    void givenExistingUser_WhenUpdated_ThenUserIsUpdated() {
        Users user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        user = usersRepository.save(user);
        Long userId = user.getId();
        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest(userId, "Updated User", "updated@example.com", "newpassword");

        BaseResponse<UserResponse> response = usersService.update(updateRequest);

        assertEquals("User updated", response.status());
        assertEquals("Updated User", response.data().name());
        assertEquals("updated@example.com", response.data().email());

        Optional<Users> updatedUser = usersRepository.findById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals("Updated User", updatedUser.get().getName());
        assertEquals("updated@example.com", updatedUser.get().getEmail());
    }

    @Test
    @DisplayName("Given non-existing user When updated Then throw exception")
    void givenNonExistingUser_WhenUpdated_ThenThrowException() {
        Long nonExistingUserId = 999L;
        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest(nonExistingUserId, "New Name", "new@example.com", "password");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.update(updateRequest);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Given existing admin When blocking user Then user is blocked")
    void givenExistingAdmin_WhenBlockingUser_ThenUserIsBlocked() {
        Users admin = new Users(null, "admin1", "admin1@example.com", "password", Role.ADMIN, false);
        usersRepository.save(admin);
        Long adminId = 1L;
        Users user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        Long userId = 2L;

        BaseResponse<Void> response = usersService.blockUser(adminId, userId);

        assertEquals("User blocked", response.status());

        Optional<Users> blockedUser = usersRepository.findById(userId);
        assertTrue(blockedUser.isPresent());
        assertTrue(blockedUser.get().isBlocked());
    }

    @Test
    @DisplayName("Given non-existing admin When blocking user Then throw exception")
    void givenNonExistingAdmin_WhenBlockingUser_ThenThrowException() {
        Long nonExistingAdminId = 999L;
        Users user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        Long userId = 2L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.blockUser(nonExistingAdminId, userId);
        });
        assertEquals("Admin not found", exception.getMessage());
    }

    @Test
    @DisplayName("Given user is not admin When blocking user Then throw exception")
    void givenUserIsNotAdmin_WhenBlockingUser_ThenThrowException() {
        Users user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        Long userId = 2L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.blockUser(userId, userId);
        });
        assertEquals("Admin not found", exception.getMessage());
    }

    @Test
    @DisplayName("Given existing admin When deleting user Then user is deleted")
    void givenExistingAdmin_WhenDeletingUser_ThenUserIsDeleted() {
        Users user = new Users(null, "testuser", "test@example.com", "password", Role.ADMIN, false);
        usersRepository.save(user);
        user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        Long adminId = 1L;
        Long userId = 2L;

        BaseResponse<Void> response = usersService.deleteUserByAdmin(adminId, userId);


        assertEquals("User deleted", response.status());

        Optional<Users> deletedUser = usersRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Given non-existing admin When deleting user Then throw exception")
    void givenNonExistingAdmin_WhenDeletingUser_ThenThrowException() {
        Users user = new Users(null, "testuser", "test@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        Long nonExistingAdminId = 999L;
        Long userId = 2L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.deleteUserByAdmin(nonExistingAdminId, userId);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Given user is not admin When deleting user Then throw exception")
    void givenUserIsNotAdmin_WhenDeletingUser_ThenThrowException() {
        Users user = new Users(null, "testuser", "test@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        Long userId = 2L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.deleteUserByAdmin(userId, userId);
        });
        assertEquals("Forbidden", exception.getMessage());
    }

    @Test
    @DisplayName("Given existing admin When deleting non-existing user Then throw exception")
    void givenExistingAdmin_WhenDeletingNonExistingUser_ThenThrowException() {
        Users user = new Users(null, "testuser", "test@example.com", "password", Role.ADMIN, false);
        usersRepository.save(user);
        user = new Users(null, "testuser1", "test1@example.com", "password", Role.USER, false);
        usersRepository.save(user);
        Long adminId = 1L;
        Long nonExistingUserId = 999L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.deleteUserByAdmin(adminId, nonExistingUserId);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Given existing user When set admin role Then user role is updated to admin")
    void givenExistingUser_WhenSetAdminRole_ThenUserRoleIsUpdatedToAdmin() {
        Users user = new Users(null, "testuser", "test@example.com", "password", Role.ADMIN, false);
        usersRepository.save(user);
        String email = "test@example.com";

        usersService.setAdminRole(email);

        Optional<Users> updatedUser = usersRepository.findByEmail(email);
        assertTrue(updatedUser.isPresent());
        assertEquals(Role.ADMIN, updatedUser.get().getRole());
    }

    @Test
    @DisplayName("Given non-existing user When set admin role Then throw exception")
    void givenNonExistingUser_WhenSetAdminRole_ThenThrowException() {
        String nonExistingEmail = "nonexistent@example.com";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.setAdminRole(nonExistingEmail);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Given valid sign up request When sign up Then user is created")
    void givenValidSignUpRequest_WhenSignUp_ThenUserIsCreated() {
        SignUpRequest signUpRequest = new SignUpRequest("Test User", "test@example.com", "password");

        BaseResponse<UserResponse> response = authenticationService.signUp(signUpRequest);

        assertEquals("User created", response.status());
        assertNotNull(response.data());
        assertEquals("Test User", response.data().name());
        assertEquals("test@example.com", response.data().email());

        Optional<Users> createdUser = usersRepository.findByEmail("test@example.com");
        assertTrue(createdUser.isPresent());
        assertEquals("Test User", createdUser.get().getName());
    }

    @Test
    @DisplayName("Given existing email When sign up Then throw exception")
    void givenExistingEmail_WhenSignUp_ThenThrowException() {
        SignUpRequest signUpRequest = new SignUpRequest("Test User", "test@example.com", "password");
        authenticationService.signUp(signUpRequest);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.signUp(signUpRequest);
        });
        assertEquals("Users creation failed", exception.getMessage());
    }

    @Test
    @DisplayName("Given valid credentials When sign in Then user is logged in")
    void givenValidCredentials_WhenSignIn_ThenUserIsLoggedIn() {
        SignUpRequest signUpRequest = new SignUpRequest("Test User", "test@example.com", "password");
        authenticationService.signUp(signUpRequest);
        SignInRequest signInRequest = new SignInRequest("test@example.com", "password");

        BaseResponse<UserResponse> response = authenticationService.signIn(signInRequest);

        assertEquals("User successfully logged in", response.status());
        assertNotNull(response.data());
        assertEquals("Test User", response.data().name());
        assertEquals("test@example.com", response.data().email());
    }

    @Test
    @DisplayName("Given non-existing email When sign in Then throw exception")
    void givenNonExistingEmail_WhenSignIn_ThenThrowException() {
        SignInRequest signInRequest = new SignInRequest("nonexistent@example.com", "password");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.signIn(signInRequest);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Given wrong password When sign in Then throw exception")
    void givenWrongPassword_WhenSignIn_ThenThrowException() {
        SignUpRequest signUpRequest = new SignUpRequest("Test User", "test@example.com", "password");
        authenticationService.signUp(signUpRequest);

        SignInRequest signInRequest = new SignInRequest("test@example.com", "wrongpassword");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.signIn(signInRequest);
        });
        assertEquals("Wrong password", exception.getMessage());
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }
}
