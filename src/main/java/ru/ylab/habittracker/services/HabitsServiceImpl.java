package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.DayStatistic;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.UsersRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HabitsServiceImpl implements HabitsService {
    private final UsersRepository usersRepository;
    private final HabitsRepository habitsRepository;

    public HabitsServiceImpl(UsersRepository usersRepository, HabitsRepository habitsRepository) {
        this.usersRepository = usersRepository;
        this.habitsRepository = habitsRepository;
    }

    @Override
    public BaseResponse<Habit> create(Habit habit) {
        Optional<User> optionalUser = usersRepository.findByEmail(habit.getCreatedBy());
        if (optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "The user was not found.", null);
        }
        User user = optionalUser.get();
        habit = habitsRepository.save(habit);
        user.getHabits().add(habit);
        return new BaseResponse<>(true, "The habit was successfully created.", habit);
    }

    @Override
    public BaseResponse<Habit> update(Habit habit) {
        Optional<User> optionalUser = usersRepository.findByEmail(habit.getCreatedBy());
        if (optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "The user was not found.", null);
        }
        Optional<Habit> optionalHabit = habitsRepository.findById(habit.getId());
        if (optionalHabit.isEmpty()) {
            return new BaseResponse<>(false, "The habit was not found.", null);
        }
        Habit result = habitsRepository.update(habit);
        return new BaseResponse<>(true, "The habit was successfully updated.", result);
    }

    @Override
    public void delete(Long id) {
        Optional<Habit> optionalHabit = habitsRepository.findById(id);
        if (optionalHabit.isEmpty()) {
            return;
        }
        Habit habit = optionalHabit.get();
        Optional<User> optionalUser = usersRepository.findByEmail(habit.getCreatedBy());
        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        user.getHabits().remove(habit);
        habitsRepository.deleteById(id);
    }

    @Override
    public BaseResponse<List<Habit>> findByEmail(String email) {
        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "The user was not found.", null);
        }
        User user = optionalUser.get();
        List<Habit> habits = user.getHabits();
        return new BaseResponse<>(true, "The habits found.", habits);
    }

    @Override
    public BaseResponse<List<Habit>> findByEmailAndDate(String email, LocalDate localDate) {
        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "The user was not found.", null);
        }
        User user = optionalUser.get();
        List<Habit> habits = user.getHabits().stream()
                .filter(habit -> localDate.equals(habit.getCreatedDate())).toList();
        return new BaseResponse<>(true, "The habits found.", habits);
    }

    @Override
    public void markCompletion(Long id) {
        habitsRepository.findById(id).ifPresent(habit -> habit.getCompletionHistory().add(LocalDate.now()));
    }

    @Override
    public BaseResponse<List<LocalDate>> showTheHistory(Long id) {
        Optional<Habit> optionalHabit = habitsRepository.findById(id);
        if (optionalHabit.isEmpty()) {
            return new BaseResponse<>(false, "The habit was not found.", null);
        }
        Habit habit = optionalHabit.get();
        List<LocalDate> habitHistory = habit.getCompletionHistory().stream().toList();
        return new BaseResponse<>(true, "The habit history found.", habitHistory);
    }

    @Override
    public BaseResponse<List<DayStatistic>> getCompletion(Long id, String period) {
        BaseResponse<List<LocalDate>> baseResponseShowTheHistory = showTheHistory(id);
        if(!baseResponseShowTheHistory.isSuccess()) {
            return new BaseResponse<>(false, baseResponseShowTheHistory.getMessage(), null);
        }
        List<LocalDate> habitHistory = baseResponseShowTheHistory.getData();
        LocalDate localDate = LocalDate.now();
        return switch (period) {
            case "month" -> getCompletionForMonth(habitHistory, localDate);
            case "week" -> getCompletionForWeek(habitHistory, localDate);
            case "day" -> getCompletionForDay(habitHistory, localDate);
            default -> new BaseResponse<>(false, "Invalid period.", null);
        };
    }

    @Override
    public BaseResponse<List<DayStatistic>> getCompletionForMonth(List<LocalDate> habits, LocalDate localDate) {
        LocalDate monthStart = localDate.withDayOfMonth(1);

        LocalDate monthEnd = localDate.withDayOfMonth(localDate.lengthOfMonth());

        List<DayStatistic> dayStatisticList = new ArrayList<>();
        for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
            boolean completed = habits.contains(date);
            dayStatisticList.add(new DayStatistic(date, completed));
        }
        return new BaseResponse<>(true, "The month statistics found.", dayStatisticList);
    }

    @Override
    public BaseResponse<List<DayStatistic>> getCompletionForWeek(List<LocalDate> habits, LocalDate localDate) {
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<DayStatistic> dayStatisticList = new ArrayList<>();
        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            boolean completed = habits.contains(date);
            dayStatisticList.add(new DayStatistic(date, completed));
        }
        return new BaseResponse<>(true, "Generated statistics for the week.", dayStatisticList);
    }

    @Override
    public BaseResponse<List<DayStatistic>> getCompletionForDay(List<LocalDate> habits, LocalDate localDate) {
        DayStatistic dayStatistic = new DayStatistic(localDate, false);
        List<DayStatistic> result = new ArrayList<>();
        result.add(dayStatistic);
        for(LocalDate date : habits) {
            if(date.equals(localDate)) {
                dayStatistic.setCompleted(true);
                return new BaseResponse<>(true, "The day was found.", result);
            }
        }
        return new BaseResponse<>(true, "The day was found.", result);
    }
}
