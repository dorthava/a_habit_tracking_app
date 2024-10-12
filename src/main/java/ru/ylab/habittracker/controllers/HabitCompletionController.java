package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.HabitReportResponse;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.services.HabitCompletionService;

import java.time.LocalDate;
import java.util.List;

public class HabitCompletionController {
    private final HabitCompletionService habitCompletionService;

    public HabitCompletionController(HabitCompletionService habitCompletionService) {
        this.habitCompletionService = habitCompletionService;
    }

    public void markCompletion(Long id) {
        habitCompletionService.markCompletion(id);
    }

    public BaseResponse<List<HabitCompletion>> showTheHistory(Long id) {
        return habitCompletionService.showTheHistory(id);
    }

    public BaseResponse<List<HabitCompletion>> getStatistics(Long id, String period) {
        return habitCompletionService.getCompletion(id, period);
    }


    public BaseResponse<Integer> calculateCurrentStreak(Long id, LocalDate currentDate) {
        return habitCompletionService.calculateCurrentStreak(id, currentDate);
    }

    public BaseResponse<Double> calculateCompletionPercentage(Long id, LocalDate periodStart, LocalDate periodEnd) {
        return habitCompletionService.calculateCompletionPercentage(id, periodStart, periodEnd);
    }

    public BaseResponse<HabitReportResponse> generateHabitReport(Long id, LocalDate periodStart, LocalDate periodEnd) {
        return habitCompletionService.generateHabitReport(id, periodStart, periodEnd);
    }
}
