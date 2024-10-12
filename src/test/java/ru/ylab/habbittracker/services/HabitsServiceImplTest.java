package ru.ylab.habbittracker.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.HabitsRepositoryImpl;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.repositories.UsersRepositoryImpl;
import ru.ylab.habittracker.services.HabitsServiceImpl;
import ru.ylab.habittracker.utils.Frequency;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HabitsServiceImplTest {
    private HabitsServiceImpl habitsService;
    private UsersRepository usersRepository;
    private HabitsRepository habitsRepository;

    @BeforeEach
    void setUp() {
        usersRepository = new UsersRepositoryImpl();
        habitsRepository = new HabitsRepositoryImpl();
        habitsService = new HabitsServiceImpl(usersRepository, habitsRepository);
    }

    @Test
    void testCreateHabitSuccess() {
        User user = new User(null, "John", "john@example.com", "password");
        user = usersRepository.save(user);

        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.DAILY, user.getEmail());

        BaseResponse<Habit> response = habitsService.create(habit);

        assertTrue(response.success());
        assertEquals(habit.getId(), response.data().getId());
        assertEquals(1, user.getHabits().size());
    }

    @Test
    void testCreateHabitUserNotFound() {
        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.WEEKLY, "unknown@example.com");

        BaseResponse<Habit> response = habitsService.create(habit);

        assertFalse(response.success());
        assertEquals("The user was not found.", response.message());
        assertNull(response.data());
    }

    @Test
    void testUpdateHabitSuccess() {
        User user = new User(null, "John", "john@example.com", "password");
        usersRepository.save(user);
        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.WEEKLY, user.getEmail());
        habitsRepository.save(habit);

        habit.setDescription("Updated daily exercise");
        BaseResponse<Habit> response = habitsService.update(habit);

        assertTrue(response.success());
        assertEquals("The habit was successfully updated.", response.message());
        assertEquals("Updated daily exercise", response.data().getDescription());
    }

    @Test
    void testUpdateHabitNotFound() {
        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.DAILY, "john@example.com");

        BaseResponse<Habit> response = habitsService.update(habit);

        assertFalse(response.success());
        assertEquals("The user was not found.", response.message());
        assertNull(response.data());
    }

    @Test
    void testDeleteHabitSuccess() {
        User user = new User(1L, "John", "john@example.com", "password");
        usersRepository.save(user);
        Habit habit = new Habit(1L, "Exercise", "Daily exercise", Frequency.DAILY, user.getEmail());
        habitsRepository.save(habit);

        habitsService.delete(habit.getId());

        assertFalse(habitsRepository.findById(habit.getId()).isPresent());
        assertFalse(user.getHabits().contains(habit));
    }

    @Test
    void testDeleteHabitNotFound() {
        habitsService.delete(999999999L);

        assertTrue(true);
    }

    @Test
    void testFindByEmailSuccess() {
        User user = new User(null, "John", "john@example.com", "password");
        user = usersRepository.save(user);
        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.DAILY, user.getEmail());

        habitsService.create(habit);
        BaseResponse<List<Habit>> response = habitsService.findByEmail(user.getEmail());

        assertTrue(response.success());
        assertEquals("The habits found.", response.message());
        assertEquals(1, response.data().size());
    }

    @Test
    void testFindByEmailUserNotFound() {
        BaseResponse<List<Habit>> response = habitsService.findByEmail("unknown@example.com");

        assertFalse(response.success());
        assertEquals("The user was not found.", response.message());
        assertNull(response.data());
    }

    @Test
    void testFindByEmailAndDateSuccess() {
        User user = new User(null, "John", "john@example.com", "password");
        usersRepository.save(user);
        LocalDate today = LocalDate.now();
        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.DAILY, user.getEmail());
        habitsService.create(habit);

        BaseResponse<List<Habit>> response = habitsService.findByEmailAndDate(user.getEmail(), today);

        assertTrue(response.success());
        assertEquals("The habits found.", response.message());
        assertEquals(1, response.data().size());
    }

    @Test
    void testFindByEmailAndDateUserNotFound() {
        BaseResponse<List<Habit>> response = habitsService.findByEmailAndDate("unknown@example.com", LocalDate.now());

        assertFalse(response.success());
        assertEquals("The user was not found.", response.message());
        assertNull(response.data());
    }
}
