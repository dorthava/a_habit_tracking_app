package ru.ylab.habbittracker.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.repositories.HabitsRepositoryImpl;
import ru.ylab.habittracker.utils.Frequency;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HabitsRepositoryImplTest {
    private HabitsRepositoryImpl habitsRepository;

    @BeforeEach
    public void setUp() {
        habitsRepository = new HabitsRepositoryImpl();
    }

    @Test
    public void testSaveHabit() {
        Habit testHabit = new Habit(null, "Test Habit", "Description of the test habit", Frequency.DAILY, "email_test@gmail.com");
        Habit habit = habitsRepository.save(testHabit);

        assertNotNull(habit.getId(), "ID should be generated");
    }

    @Test
    public void testFindAllShouldReturnAllHabits() {
        Habit testHabit = new Habit(null, "Test Habit", "Description of the test habit", Frequency.DAILY, "email_test@gmail.com");
        habitsRepository.save(testHabit);

        List<Habit> habits = habitsRepository.findAll();

        assertEquals(habits.size(), 1);
    }

    @Test
    public void testSaveShouldGenerateIdAndReturnHabit() {
        Habit testHabit = new Habit(null, "Test Habit", "Description of the test habit", Frequency.DAILY, "email_test@gmail.com");
        Habit savedHabit = habitsRepository.save(testHabit);

        assertNotNull(savedHabit.getId(), "ID should be generated");
        assertEquals("Test Habit", savedHabit.getName());
        assertEquals("Description of the test habit", savedHabit.getDescription());
    }

    @Test
    public void testUpdateShouldUpdateHabitDetails() {
        Habit testHabit = new Habit(null, "Test Habit", "Description of the test habit", Frequency.DAILY, "email_test@gmail.com");
        testHabit = habitsRepository.save(testHabit);
        Long id = testHabit.getId();
        Habit testHabit2 = new Habit(id, "Test Habit2", "Description of the test habit2", Frequency.DAILY, "email_test@gmail.com");
        Habit updatedHabit = habitsRepository.update(testHabit2);

        assertEquals("Test Habit2", testHabit.getName());
    }


    @Test
    public void testDeleteByIdShouldRemoveHabit() {
        Habit testHabit = new Habit(null, "Test Habit", "Description of the test habit", Frequency.DAILY, "email_test@gmail.com");
        Habit savedHabit = habitsRepository.save(testHabit);
        Long habitId = savedHabit.getId();
        habitsRepository.delete(habitId);

        Optional<Habit> deletedHabit = habitsRepository.findById(habitId);
        assertFalse(deletedHabit.isPresent());
    }

    @Test
    public void testFindByIdShouldReturnExistingHabit() {
        Habit testHabit = new Habit(null, "Test Habit", "Description of the test habit", Frequency.DAILY, "email_test@gmail.com");
        Habit savedHabit = habitsRepository.save(testHabit);
        Long habitId = savedHabit.getId();
        Optional<Habit> foundHabit = habitsRepository.findById(habitId);

        assertTrue(foundHabit.isPresent());
        assertEquals(testHabit, foundHabit.get());
    }

    @Test
    public void testDeleteByEmailShouldRemoveHabitsCreatedByEmail() {
        Habit testHabit = new Habit(null, "Test Habit", "Description of the test habit", Frequency.DAILY, "test@gmail.com");
        habitsRepository.save(testHabit);
        habitsRepository.delete("test@gmail.com");

        List<Habit> habits = habitsRepository.findAll();
        assertEquals(habits.size(), 0);
    }
}
