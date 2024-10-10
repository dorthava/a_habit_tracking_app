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
        return habitsService.create(habit);
    }

    public BaseResponse<Habit> update(Habit habit) {
        return habitsService.update(habit);
    }

    public void delete(Long id) {
        habitsService.delete(id);
    }

    public BaseResponse<List<Habit>> findAllUserHabitsByEmail(String email) {
        return habitsService.findByEmail(email);
    }

    public BaseResponse<List<Habit>> findAllUserHabitsByEmailAndDate(String email, LocalDate date) {
        return habitsService.findByEmailAndDate(email, date);
    }
}
