package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.HabitReportResponse;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.repositories.HabitCompletionRepository;
import ru.ylab.habittracker.repositories.HabitsRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HabitCompletionServiceImpl implements HabitCompletionService {
    private final HabitsRepository habitsRepository;
    private final HabitCompletionRepository habitCompletionRepository;

    public HabitCompletionServiceImpl(HabitsRepository habitsRepository, HabitCompletionRepository habitCompletionRepository) {
        this.habitsRepository = habitsRepository;
        this.habitCompletionRepository = habitCompletionRepository;
    }

    @Override
    public HabitCompletion markCompletion(Long id) {
        Optional<Habit> optionalHabit = habitsRepository.findById(id);
        if (optionalHabit.isEmpty()) {
            return null;
        }

        Habit habit = optionalHabit.get();
        LocalDate today = LocalDate.now();

        List<HabitCompletion> habitCompletionList = habit.getCompletions();
        boolean alreadyCompleted = habitCompletionList.stream()
                .anyMatch(completion -> completion.getCompletionDate().equals(today));

        if (alreadyCompleted) {
            return null;
        }

        HabitCompletion habitCompletion = new HabitCompletion(today, true, id);
        habitCompletionRepository.save(habitCompletion);
        habitCompletionList.add(habitCompletion);

        return habitCompletion;
    }

    @Override
    public BaseResponse<List<HabitCompletion>> showTheHistory(Long id) {
        Optional<Habit> optionalHabit = habitsRepository.findById(id);
        if (optionalHabit.isEmpty()) {
            return new BaseResponse<>(false, "The habit was not found.", null);
        }
        Habit habit = optionalHabit.get();
        List<HabitCompletion> habitHistory = habit.getCompletions().stream().toList();
        return new BaseResponse<>(true, "The habit history found.", habitHistory);
    }

    @Override
    public BaseResponse<List<HabitCompletion>> getCompletion(Long id, String period) {
        BaseResponse<List<HabitCompletion>> baseResponseShowTheHistory = showTheHistory(id);
        if (!baseResponseShowTheHistory.success()) {
            return new BaseResponse<>(false, baseResponseShowTheHistory.message(), null);
        }
        List<HabitCompletion> habitHistory = baseResponseShowTheHistory.data();
        LocalDate localDate = LocalDate.now();
        return switch (period) {
            case "month" -> getCompletionForMonth(id, habitHistory, localDate);
            case "week" -> getCompletionForWeek(id, habitHistory, localDate);
            case "day" -> getCompletionForDay(id, habitHistory, localDate);
            default -> new BaseResponse<>(false, "Invalid period.", null);
        };
    }

    @Override
    public BaseResponse<List<HabitCompletion>> getCompletionForMonth(Long habitId, List<HabitCompletion> habitCompletions,
                                                                     LocalDate localDate) {
        LocalDate monthStart = localDate.withDayOfMonth(1);

        LocalDate monthEnd = localDate.withDayOfMonth(localDate.lengthOfMonth());

        List<HabitCompletion> dayStatisticList = findHabitCompletionsOfPeriod(habitId, habitCompletions, monthStart, monthEnd);
        return new BaseResponse<>(true, "The month statistics found.", dayStatisticList);
    }

    private List<HabitCompletion> findHabitCompletionsOfPeriod(Long habitId, List<HabitCompletion> habitCompletions, LocalDate start, LocalDate end) {
        List<HabitCompletion> dayStatisticList = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            HabitCompletion completionForDay = habitCompletions.stream()
                    .filter(hc -> hc.getCompletionDate().equals(finalDate))
                    .findFirst()
                    .orElse(null);

            if (completionForDay == null) {
                HabitCompletion missingCompletion = new HabitCompletion(finalDate, habitId);
                dayStatisticList.add(missingCompletion);
            } else {
                dayStatisticList.add(completionForDay);
            }
        }
        return dayStatisticList;
    }

    @Override
    public BaseResponse<List<HabitCompletion>> getCompletionForWeek(Long habitId, List<HabitCompletion> habitCompletions,
                                                                    LocalDate localDate) {
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<HabitCompletion> dayStatisticList = findHabitCompletionsOfPeriod(habitId, habitCompletions, weekStart, weekEnd);
        return new BaseResponse<>(true, "Generated statistics for the week.", dayStatisticList);
    }

    @Override
    public BaseResponse<List<HabitCompletion>> getCompletionForDay(Long habitId, List<HabitCompletion> habitCompletions,
                                                                   LocalDate localDate) {
        List<HabitCompletion> dayStatisticList = new ArrayList<>();
        dayStatisticList.add(new HabitCompletion(localDate, false, habitId));
        for (HabitCompletion habitCompletion : habitCompletions) {
            if (habitCompletion.getCompletionDate().equals(localDate)) {
                dayStatisticList.get(0).setCompleted(true);
                return new BaseResponse<>(true, "Generated statistics for the day.", dayStatisticList);
            }
        }
        return new BaseResponse<>(true, "Generated statistics for the day.", dayStatisticList);
    }

    @Override
    public BaseResponse<Integer> calculateCurrentStreak(Long id, LocalDate currentDate) {
        List<HabitCompletion> habitCompletionList = getHabitCompletions(id);
        if(habitCompletionList == null) {
            return new BaseResponse<>(false, "Habit completion list is empty.", null);
        }
        int result = 0;
        for (int i = habitCompletionList.size() - 1; i >= 0; --i) {
            HabitCompletion completion = habitCompletionList.get(i);
            if (completion.getCompletionDate().equals(currentDate)) {
                ++result;
                currentDate = currentDate.minusDays(1);
            } else {
                break;
            }
        }

        return new BaseResponse<>(true, "Current streak found.", result);
    }

    @Override
    public BaseResponse<Double> calculateCompletionPercentage(Long id, LocalDate periodStart, LocalDate periodEnd) {
        List<HabitCompletion> habitCompletionList = getHabitCompletions(id);
        if(habitCompletionList == null) {
            return new BaseResponse<>(false, "Habit completion list is empty.", null);
        }

        long totalDaysInPeriod = calculateTotalDays(periodStart, periodEnd);
        long countDaysInPeriod = calculateCountDaysInPeriod(habitCompletionList, periodStart, periodEnd);
        System.out.println();
        return new BaseResponse<>(true, "Found completion percentage.", (double) countDaysInPeriod / totalDaysInPeriod);
    }

    @Override
    public long calculateTotalDays(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public long calculateCountDaysInPeriod(List<HabitCompletion> habitCompletionList, LocalDate startDate, LocalDate endDate) {
        long countDaysInPeriod = 0;
        for (HabitCompletion habitCompletion : habitCompletionList) {
            LocalDate completionDate = habitCompletion.getCompletionDate();
            if ((completionDate.isEqual(startDate) || completionDate.isAfter(startDate)) &&
                    (completionDate.isEqual(endDate) || completionDate.isBefore(endDate))) {
                ++countDaysInPeriod;
            }
        }
        return countDaysInPeriod;
    }

    @Override
    public BaseResponse<HabitReportResponse> generateHabitReport(Long id, LocalDate periodStart, LocalDate periodEnd) {
        List<HabitCompletion> habitCompletionList = getHabitCompletions(id);
        if(habitCompletionList == null) {
            return new BaseResponse<>(false, "Habit completion list is empty.", null);
        }

        BaseResponse<Double> completionPercentage = calculateCompletionPercentage(id, periodStart, periodEnd);
        BaseResponse<Integer> currentStreak = calculateCurrentStreak(id, periodStart);
        Optional<Habit> optionalHabit = habitsRepository.findById(id);
        long countDaysInPeriod = calculateCountDaysInPeriod(habitCompletionList, periodStart, periodEnd);
        HabitReportResponse habitReportResponse = new HabitReportResponse(optionalHabit.get().getName(), currentStreak.data(),
                countDaysInPeriod, completionPercentage.data());
        return new BaseResponse<>(true, "Success!", habitReportResponse);
    }

    private List<HabitCompletion> getHabitCompletions(Long id) {
        Optional<Habit> optionalHabit = habitsRepository.findById(id);
        if(optionalHabit.isEmpty()) {
            return null;
        }
        Habit habit = optionalHabit.get();
        return habit.getCompletions();
    }
}
