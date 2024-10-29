package ru.ylab.habittracker.controllers;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.services.HabitsService;

import java.time.LocalDate;
import java.util.List;

public class HabitsController {
    private final HabitsService habitsService;

    public HabitsController(HabitsService habitsService) {
        this.habitsService = habitsService;
    }

    public BaseResponse<Habit> create(Habit habit) {
        try {
            return habitsService.create(habit);
        } catch (RuntimeException e) {
            return new BaseResponse<>(e.getMessage(), null);
        }
    }

    public BaseResponse<Habit> update(Habit habit) {
        try {
            return habitsService.update(habit);
        } catch (RuntimeException e) {
            return new BaseResponse<>(e.getMessage(), null);
        }
    }

    public void delete(Long id) {
        habitsService.delete(id);
    }

    public BaseResponse<List<Habit>> findAllUserHabitsById(Long userId) {
        try {
            return habitsService.findByUserId(userId);
        } catch (RuntimeException e) {
            return new BaseResponse<>(e.getMessage(), null);
        }
    }

    public BaseResponse<List<Habit>> findAllUserHabitsByUserIdAndDate(Long userId, LocalDate date) {
        try {
            return habitsService.findByUserIdAndDate(userId, date);
        } catch (RuntimeException e) {
            return new BaseResponse<>(e.getMessage(), null);
        }
    }
}
