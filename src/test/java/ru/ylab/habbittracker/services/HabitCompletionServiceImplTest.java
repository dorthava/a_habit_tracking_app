package ru.ylab.habbittracker.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.HabitReportResponse;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.repositories.HabitCompletionRepository;
import ru.ylab.habittracker.repositories.HabitCompletionRepositoryImpl;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.HabitsRepositoryImpl;
import ru.ylab.habittracker.services.HabitCompletionServiceImpl;
import ru.ylab.habittracker.utils.Frequency;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HabitCompletionServiceImplTest {
    private HabitsRepository habitsRepository;
    private HabitCompletionRepository habitCompletionRepository;
    private HabitCompletionServiceImpl habitCompletionService;
    private Habit testHabit;
    private List<HabitCompletion> habitCompletions;

    @BeforeEach
    void setUp() {
        habitsRepository = new HabitsRepositoryImpl();
        habitCompletionRepository = new HabitCompletionRepositoryImpl();
        habitCompletionService = new HabitCompletionServiceImpl(habitsRepository, habitCompletionRepository);
        testHabit = new Habit(null, "Test Habit", "test@example.com", Frequency.DAILY, "test@gmail.com");
        testHabit = habitsRepository.save(testHabit);
        habitCompletions = new ArrayList<>();
    }

    @Test
    void testMarkCompletion() {
        habitCompletionService.markCompletion(testHabit.getId());
        List<HabitCompletion> completions = testHabit.getCompletions();
        assertEquals(1, completions.size());
        assertTrue(completions.get(0).isCompleted());
        assertEquals(LocalDate.now(), completions.get(0).getCompletionDate());
    }

    @Test
    void testShowTheHistory() {
        habitCompletionService.markCompletion(testHabit.getId());
        BaseResponse<List<HabitCompletion>> response = habitCompletionService.showTheHistory(testHabit.getId());
        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals(1, response.data().size());
    }

    @Test
    void testGetCompletionForMonth() {
        habitCompletionService.markCompletion(testHabit.getId());
        BaseResponse<List<HabitCompletion>> response = habitCompletionService.getCompletion(testHabit.getId(), "month");
        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals(31, response.data().size());
    }

    @Test
    void testCalculateCurrentStreak() {
        HabitCompletion prevHabitCompletion = habitCompletionService.markCompletion(testHabit.getId());
        prevHabitCompletion.setCompletionDate(LocalDate.now().minusDays(1));
        habitCompletionService.markCompletion(testHabit.getId());
        BaseResponse<Integer> response = habitCompletionService.calculateCurrentStreak(testHabit.getId(), LocalDate.now());
        assertTrue(response.success());
        assertEquals(2, response.data());
    }

    @Test
    void testCalculateCompletionPercentage() {
        habitCompletionService.markCompletion(testHabit.getId());
        BaseResponse<Double> response = habitCompletionService.calculateCompletionPercentage(testHabit.getId(), LocalDate.now().minusDays(1), LocalDate.now());
        assertTrue(response.success());
        assertEquals(0.5, response.data());
    }

    @Test
    void testGenerateHabitReport() {
        habitCompletionService.markCompletion(testHabit.getId());
        BaseResponse<HabitReportResponse> response = habitCompletionService.generateHabitReport(testHabit.getId(), LocalDate.now().minusDays(1), LocalDate.now());
        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals(testHabit.getName(), response.data().getHabitName());
    }

    @Test
    void testGenerateHabitReport2() {
        habitCompletionService.markCompletion(testHabit.getId());

        BaseResponse<HabitReportResponse> response = habitCompletionService.generateHabitReport(
                testHabit.getId(),
                LocalDate.now().minusDays(1),
                LocalDate.now()
        );

        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals(testHabit.getName(), response.data().getHabitName());
        assertEquals(1, response.data().getTotalCompletions());
    }

    @Test
    void testGetCompletionForWeek() {
        Long habitId = 1L;
        LocalDate today = LocalDate.now();
        habitCompletions.add(new HabitCompletion(today.minusDays(1), true, habitId));
        habitCompletions.add(new HabitCompletion(today.minusDays(2), false, habitId));
        habitCompletions.add(new HabitCompletion(today.minusDays(3), true, habitId));
        habitCompletions.add(new HabitCompletion(today.minusDays(4), false, habitId));

        BaseResponse<List<HabitCompletion>> response = habitCompletionService.getCompletionForWeek(habitId, habitCompletions, today);

        assertTrue(response.success());
        assertNotNull(response.data());

        assertEquals(7, response.data().size());
        assertEquals("Generated statistics for the week.", response.message());
    }

    @Test
    void testGetCompletionForDay() {
        Long habitId = 2L;
        LocalDate today = LocalDate.now();
        habitCompletions.add(new HabitCompletion(today, true, habitId));
        habitCompletions.add(new HabitCompletion(today.minusDays(1), false, habitId));

        BaseResponse<List<HabitCompletion>> response = habitCompletionService.getCompletionForDay(habitId, habitCompletions, today);

        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals(1, response.data().size());
        assertTrue(response.data().get(0).isCompleted());
        assertEquals("Generated statistics for the day.", response.message());
    }
}
