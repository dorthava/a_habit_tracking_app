package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.DayStatistic;
import ru.ylab.habittracker.models.Habit;

import java.time.LocalDate;
import java.util.List;

public interface HabitsService {
    BaseResponse<Habit> create(Habit habit);

    BaseResponse<Habit> update(Habit habit);

    void delete(Long id);

    BaseResponse<List<Habit>> findByEmail(String email);

    BaseResponse<List<Habit>> findByEmailAndDate(String email, LocalDate localDate);

    void markCompletion(Long id);

    BaseResponse<List<LocalDate>> showTheHistory(Long id);

    BaseResponse<List<DayStatistic>> getCompletion(Long id, String period);

    BaseResponse<List<DayStatistic>> getCompletionForMonth(List<LocalDate> habits, LocalDate localDate);

    BaseResponse<List<DayStatistic>> getCompletionForWeek(List<LocalDate> habits, LocalDate localDate);

    BaseResponse<List<DayStatistic>> getCompletionForDay(List<LocalDate> habits, LocalDate localDate);
}
