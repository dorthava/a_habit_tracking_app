package ru.ylab.habbittracker.repositories;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.UsersRepositoryImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UsersRepositoryImplTest {
    private static UsersRepositoryImpl usersRepository;

    @BeforeAll
    static void setUp() {
        usersRepository = new UsersRepositoryImpl();
    }

    @Test
    void testSaveUser() {
        User user = new User(null, "Test User", "test@example.com", "password");

        User savedUser = usersRepository.save(user);

        assertNotNull(savedUser.getId(), "ID should be generated");
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test User", savedUser.getName());
        assertEquals("password", savedUser.getPassword());
    }

    @Test
    void testFindByEmailUserExists() {
        User user = new User(null, "Test User", "test@example.com", "password");
        usersRepository.save(user);

        Optional<User> foundUser = usersRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals("Test User", foundUser.get().getName());
    }

    @Test
    void testFindByEmailUserDoesNotExist() {
        Optional<User> foundUser = usersRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent(), "User should not be found");
    }

    @Test
    void testUpdateUser() {
        User user = new User(null, "Test User", "test@example.com", "password");
        usersRepository.save(user);

        User updatedUser = new User(null, "Updated User", "test@example.com", "newpassword");
        User result = usersRepository.update(updatedUser);

        assertEquals("Updated User", result.getName());
        assertEquals("newpassword", result.getPassword());
    }

    @Test
    public void testUpdateWithEmptyNameShouldNotUpdateName() {
        User user = new User(null, "Test User", "test@example.com", "password");
        usersRepository.save(user);

        User updatedUser = new User(null, "", "test@example.com", "newpassword123");

        User result = usersRepository.update(updatedUser);

        assertEquals(result.getName(), "Test User");
        assertEquals(result.getPassword(), "newpassword123");
    }

    @Test
    void testDeleteUserById() {
        User user = new User(null, "Test User", "test@example.com", "password");
        User savedUser = usersRepository.save(user);

        usersRepository.delete(savedUser.getId());

        assertFalse(usersRepository.existsByEmail("test@example.com"), "User should be deleted");
    }

    @Test
    void testDeleteUserByEmail() {
        User user = new User(null, "Test User", "test@example.com", "password");
        usersRepository.save(user);

        usersRepository.delete("test@example.com");

        assertFalse(usersRepository.existsByEmail("test@example.com"), "User should be deleted by email");
    }

    @Test
    void testExistsByEmail() {
        User user = new User(null, "Test User", "test@example.com", "password");
        usersRepository.save(user);

        assertTrue(usersRepository.existsByEmail("test@example.com"), "User should exist");
        assertFalse(usersRepository.existsByEmail("nonexistent@example.com"), "User should not exist");
    }

    @Test
    void testFindAll() {
        User user = new User(null, "Test User", "test@example.com", "password");
        usersRepository.save(user);

        User user2 = new User(null, "Test User2", "test2@example.com", "password");
        usersRepository.save(user2);

        List<User> users = usersRepository.findAll();
        assertEquals(users.get(0), user);
        assertEquals(users.get(1), user2);
    }
}
