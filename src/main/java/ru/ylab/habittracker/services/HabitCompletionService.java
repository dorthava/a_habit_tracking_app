package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.HabitReportResponse;
import ru.ylab.habittracker.models.HabitCompletion;

import java.time.LocalDate;
import java.util.List;

public interface HabitCompletionService {
    HabitCompletion markCompletion(Long id);

    BaseResponse<List<HabitCompletion>> showTheHistory(Long id);

    BaseResponse<List<HabitCompletion>> getCompletion(Long id, String period);

    BaseResponse<List<HabitCompletion>> getCompletionForMonth(Long habitId, List<HabitCompletion> habitCompletions, LocalDate localDate);

    BaseResponse<List<HabitCompletion>> getCompletionForWeek(Long habitId, List<HabitCompletion> habitCompletions, LocalDate localDate);

    BaseResponse<List<HabitCompletion>> getCompletionForDay(Long habitId, List<HabitCompletion> habitCompletions, LocalDate localDate);

    BaseResponse<Integer> calculateCurrentStreak(Long id, LocalDate currentDate);

    BaseResponse<Double> calculateCompletionPercentage(Long id, LocalDate periodStart, LocalDate periodEnd);

    long calculateTotalDays(LocalDate startDate, LocalDate endDate);

    BaseResponse<HabitReportResponse> generateHabitReport(Long id, LocalDate periodStart, LocalDate periodEnd);
}
