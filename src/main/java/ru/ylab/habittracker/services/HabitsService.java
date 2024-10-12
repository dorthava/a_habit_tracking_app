package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.Habit;

import java.time.LocalDate;
import java.util.List;

public interface HabitsService {
    BaseResponse<Habit> create(Habit habit);

    BaseResponse<Habit> update(Habit habit);

    void delete(Long id);

    BaseResponse<List<Habit>> findByEmail(String email);

    BaseResponse<List<Habit>> findByEmailAndDate(String email, LocalDate localDate);
}
