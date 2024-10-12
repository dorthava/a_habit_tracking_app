package ru.ylab.habbittracker.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.HabitsRepositoryImpl;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.repositories.UsersRepositoryImpl;
import ru.ylab.habittracker.services.UsersServiceImpl;
import ru.ylab.habittracker.utils.Role;

import static org.junit.jupiter.api.Assertions.*;

public class UsersServiceImplTest {
    private UsersServiceImpl usersService;
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        usersRepository = new UsersRepositoryImpl();
        HabitsRepository habitsRepository = new HabitsRepositoryImpl();
        usersService = new UsersServiceImpl(usersRepository, habitsRepository);
    }

    @Test
    void testCreateUser() {
        User user = new User(null, "John", "john@example.com", "password");
        User createdUser = usersService.create(user);

        assertEquals("john@example.com", createdUser.getEmail());
        assertEquals("password", createdUser.getPassword());
    }

    @Test
    void testUpdateUserSuccess() {
        User user = new User(null, "John", "john@example.com", "password");
        usersRepository.save(user);

        user.setName("John Updated");
        BaseResponse<User> response = usersService.update(user);

        assertTrue(response.success());
        assertEquals("User updated", response.message());
        assertEquals("John Updated", response.data().getName());
    }

    @Test
    void testUpdateUserNotFound() {
        User user = new User(null, "John", "john@example.com", "password");

        BaseResponse<User> response = usersService.update(user);

        assertFalse(response.success());
        assertEquals("User not found", response.message());
        assertNull(response.data());
    }

    @Test
    void testDeleteUser() {
        usersRepository.save(new User(null, "John", "john@example.com", "password"));

        usersService.delete("john@example.com");

        assertFalse(usersRepository.existsByEmail("john@example.com"));
    }

    @Test
    void testUpdatePasswordSuccess() {
        User user = new User(null, "John", "john@example.com", "password");
        usersRepository.save(user);

        BaseResponse<User> response = usersService.updatePasswordByEmail("john@example.com", "newpassword");

        assertTrue(response.success());
        assertEquals("Password updated", response.message());
        assertEquals("newpassword", response.data().getPassword());
    }

    @Test
    void testUpdatePasswordUserNotFound() {
        BaseResponse<User> response = usersService.updatePasswordByEmail("unknown@example.com", "newpassword");

        assertFalse(response.success());
        assertEquals("User not found", response.message());
        assertNull(response.data());
    }

    @Test
    void testBlockUserAsAdmin() {
        usersRepository.save(new User(null, "John", "test@example.com", "password"));
        BaseResponse<Void> response = usersService.blockUser(Role.ADMIN, "test@example.com");
        assertTrue(response.success());
        assertEquals("User blocked", response.message());

        User blockedUser = usersRepository.findByEmail("test@example.com").get();
        assertTrue(blockedUser.isBlocked());
    }

    @Test
    void testBlockUserAsRegularUser() {
        BaseResponse<Void> response = usersService.blockUser(Role.USER, "test@example.com");
        assertFalse(response.success());
        assertEquals("Forbidden", response.message());
    }

    @Test
    void testBlockUserNotFound() {
        BaseResponse<Void> response = usersService.blockUser(Role.ADMIN, "notfound@example.com");
        assertFalse(response.success());
        assertEquals("User not found", response.message());
    }

    @Test
    void testDeleteUserAsAdmin() {
        BaseResponse<Void> response = usersService.deleteUser(Role.ADMIN, "test@example.com");
        assertTrue(response.success());
        assertEquals("User deleted", response.message());

        assertFalse(usersRepository.findByEmail("test@example.com").isPresent());
    }

    @Test
    void testDeleteUserAsRegularUser() {
        BaseResponse<Void> response = usersService.deleteUser(Role.USER, "test@example.com");
        assertFalse(response.success());
        assertEquals("Forbidden", response.message());
    }

    @Test
    void testSetAdminRole() {
        usersRepository.save(new User(null, "John", "test@example.com", "password"));
        usersService.setAdminRole("test@example.com");

        User user = usersRepository.findByEmail("test@example.com").get();
        assertEquals(Role.ADMIN, user.getRole());
    }
}
