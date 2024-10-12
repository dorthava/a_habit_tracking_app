package ru.ylab.habbittracker.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.repositories.HabitCompletionRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HabitCompletionRepositoryImplTest {
    private HabitCompletionRepositoryImpl habitCompletionRepository;

    @BeforeEach
    public void setUp() {
        habitCompletionRepository = new HabitCompletionRepositoryImpl();
    }

    @Test
    public void testSave() {
        HabitCompletion completion = new HabitCompletion(LocalDate.now(), 0L);
        completion = habitCompletionRepository.save(completion);
        assertNotNull(completion);
    }

    @Test
    public void testFindById() {
        HabitCompletion completion = new HabitCompletion(LocalDate.now(), 0L);
        completion = habitCompletionRepository.save(completion);

        Optional<HabitCompletion> foundCompletion = habitCompletionRepository.findById(completion.getId());

        assertTrue(foundCompletion.isPresent());
        assertEquals(completion.getId(), foundCompletion.get().getId());
    }

    @Test
    public void testUpdate() {
        HabitCompletion completion = new HabitCompletion(LocalDate.now(), 0L);
        HabitCompletion savedCompletion = habitCompletionRepository.save(completion);

        savedCompletion.setCompletionDate(LocalDate.now().plusDays(1));
        HabitCompletion updatedCompletion = habitCompletionRepository.update(savedCompletion);

        assertEquals(LocalDate.now().plusDays(1), updatedCompletion.getCompletionDate());
    }

    @Test
    public void testDelete() {
        HabitCompletion completion = new HabitCompletion(LocalDate.now(), 0L);
        completion.setCompletionDate(LocalDate.now());
        HabitCompletion savedCompletion = habitCompletionRepository.save(completion);

        habitCompletionRepository.delete(savedCompletion.getId());

        Optional<HabitCompletion> deletedCompletion = habitCompletionRepository.findById(savedCompletion.getId());
        assertFalse(deletedCompletion.isPresent());
    }

    @Test
    public void testFindAll() {
        HabitCompletion completion1 = new HabitCompletion(LocalDate.now(), 0L);
        HabitCompletion completion2 = new HabitCompletion(LocalDate.now(), 0L);

        habitCompletionRepository.save(completion1);
        habitCompletionRepository.save(completion2);

        List<HabitCompletion> completions = habitCompletionRepository.findAll();

        assertEquals(2, completions.size());
    }
}
