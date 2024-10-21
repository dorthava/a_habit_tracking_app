package ru.ylab.habittracker.services.impl;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.HabitReportResponse;
import ru.ylab.habittracker.exception.HabitAlreadyCompletedException;
import ru.ylab.habittracker.exception.HabitNotFoundException;
import ru.ylab.habittracker.exception.InvalidPeriodException;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.repositories.HabitCompletionRepository;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.services.HabitCompletionService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для отслеживания выполнения привычек.
 * Этот класс управляет выполнением привычек, их историей и статистикой.
 */
public class HabitCompletionServiceImpl implements HabitCompletionService {
    private final HabitsRepository habitsRepository;
    private final HabitCompletionRepository habitCompletionRepository;

    /**
     * Конструктор для создания экземпляра HabitCompletionServiceImpl.
     *
     * @param habitsRepository          репозиторий привычек.
     * @param habitCompletionRepository  репозиторий выполнения привычек.
     */
    public HabitCompletionServiceImpl(HabitsRepository habitsRepository, HabitCompletionRepository habitCompletionRepository) {
        this.habitsRepository = habitsRepository;
        this.habitCompletionRepository = habitCompletionRepository;
    }

    /**
     * Отмечает выполнение привычки по её идентификатору.
     *
     * @param id идентификатор привычки.
     * @return объект HabitCompletion, представляющий выполненную привычку.
     * @throws HabitNotFoundException          если привычка не найдена.
     * @throws HabitAlreadyCompletedException   если привычка уже выполнена на сегодняшний день.
     */
    @Override
    public HabitCompletion markCompletion(Long id) {
        Habit habit = findHabitById(id);

        LocalDate today = LocalDate.now();
        List<HabitCompletion> habitCompletions = habitCompletionRepository.findByHabitId(id);

        if (habitCompletions.stream().anyMatch(completion -> completion.getCompletionDate().equals(today))) {
            throw new HabitAlreadyCompletedException("Habit already completed");
        }

        HabitCompletion habitCompletion = new HabitCompletion(id, today, habit.getId());
        habitCompletionRepository.save(habitCompletion);

        return habitCompletion;
    }

    /**
     * Показывает историю выполнения привычки по её идентификатору.
     *
     * @param id идентификатор привычки.
     * @return объект BaseResponse, содержащий список выполнений привычки.
     * @throws HabitNotFoundException если привычка не найдена.
     */
    @Override
    public BaseResponse<List<HabitCompletion>> showTheHistory(Long id) {
        findHabitById(id);
        List<HabitCompletion> habitHistory = habitCompletionRepository.findByHabitId(id);
        return new BaseResponse<>("The habit history found.", habitHistory);
    }

    /**
     * Получает статистику выполнения привычки за указанный период.
     *
     * @param id идентификатор привычки.
     * @param period период (неделя, месяц, день).
     * @return объект BaseResponse, содержащий список выполнений привычки за указанный период.
     * @throws InvalidPeriodException если период указан неверно.
     */
    @Override
    public BaseResponse<List<HabitCompletion>> getCompletion(Long id, String period) {
        LocalDate localDate = LocalDate.now();
        return switch (period) {
            case "month" -> getCompletionForMonth(id, localDate);
            case "week" -> getCompletionForWeek(id, localDate);
            case "day" -> getCompletionForDay(id, localDate);
            default -> throw new InvalidPeriodException("Invalid period");
        };
    }

    /**
     * Находит привычку по её идентификатору.
     *
     * @param habitId идентификатор привычки.
     * @return объект Habit, если привычка найдена.
     * @throws HabitNotFoundException если привычка не найдена.
     */
    private Habit findHabitById(Long habitId) {
        return habitsRepository.findById(habitId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));
    }

    /**
     * Получает статистику выполнения привычки за месяц.
     *
     * @param habitId идентификатор привычки.
     * @param localDate дата для вычислений.
     * @return объект BaseResponse, содержащий список выполнений привычки за месяц.
     */
    @Override
    public BaseResponse<List<HabitCompletion>> getCompletionForMonth(Long habitId, LocalDate localDate) {
        LocalDate monthStart = localDate.withDayOfMonth(1);
        LocalDate monthEnd = localDate.withDayOfMonth(localDate.lengthOfMonth());

        List<HabitCompletion> monthStatistics = findHabitCompletionsOfPeriod(habitId, monthStart, monthEnd);
        return new BaseResponse<>("The month statistics found.", monthStatistics);
    }

    /**
     * Получает статистику выполнения привычки за неделю.
     *
     * @param habitId идентификатор привычки.
     * @param localDate дата для вычислений.
     * @return объект BaseResponse, содержащий список выполнений привычки за неделю.
     */
    @Override
    public BaseResponse<List<HabitCompletion>> getCompletionForWeek(Long habitId, LocalDate localDate) {
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<HabitCompletion> weekStatistics = findHabitCompletionsOfPeriod(habitId, weekStart, weekEnd);
        return new BaseResponse<>("Generated statistics for the week.", weekStatistics);
    }

    /**
     * Получает статистику выполнения привычки за день.
     *
     * @param habitId идентификатор привычки.
     * @param localDate дата для вычислений.
     * @return объект BaseResponse, содержащий список выполнений привычки за день.
     */
    @Override
    public BaseResponse<List<HabitCompletion>> getCompletionForDay(Long habitId, LocalDate localDate) {
        List<HabitCompletion> dayStatistics = findHabitCompletionsOfPeriod(habitId, localDate, localDate);
        return new BaseResponse<>("Generated statistics for the day.", dayStatistics);
    }

    /**
     * Вычисляет текущую серию выполнения привычки.
     *
     * @param id идентификатор привычки.
     * @param currentDate дата, по которой вычисляется серия.
     * @return объект BaseResponse, содержащий текущую серию.
     * @throws HabitNotFoundException если привычка не найдена.
     */
    @Override
    public BaseResponse<Integer> calculateCurrentStreak(Long id, LocalDate currentDate) {
        List<HabitCompletion> habitCompletionList = habitCompletionRepository.findByHabitId(id);
        if(habitCompletionList.isEmpty()) {
            throw new HabitNotFoundException("Habit completion list is empty.");
        }

        int streak = 0;
        for (int i = habitCompletionList.size() - 1; i >= 0; --i) {
            HabitCompletion completion = habitCompletionList.get(i);
            if (completion.getCompletionDate().equals(currentDate)) {
                ++streak;
                currentDate = currentDate.minusDays(1);
            } else {
                break;
            }
        }

        return new BaseResponse<>("Current streak found.", streak);
    }

    /**
     * Вычисляет процент выполнения привычки за указанный период.
     *
     * @param id идентификатор привычки.
     * @param periodStart начало периода.
     * @param periodEnd конец периода.
     * @return объект BaseResponse, содержащий процент выполнения привычки.
     * @throws HabitNotFoundException если привычка не найдена.
     */
    @Override
    public BaseResponse<Double> calculateCompletionPercentage(Long id, LocalDate periodStart, LocalDate periodEnd) {
        List<HabitCompletion> habitCompletionList = findHabitCompletionsOfPeriod(id, periodStart, periodEnd);
        if(habitCompletionList.isEmpty()) {
            throw new HabitNotFoundException("Habit completion list is empty.");
        }

        long totalDaysInPeriod = calculateTotalDays(periodStart, periodEnd);
        long countDaysInPeriod = calculateCountDaysInPeriod(habitCompletionList, periodStart, periodEnd);

        return new BaseResponse<>("Found completion percentage.", (double) countDaysInPeriod / totalDaysInPeriod);
    }

    /**
     * Вычисляет общее количество дней между двумя датами.
     *
     * @param startDate начало периода.
     * @param endDate конец периода.
     * @return общее количество дней между двумя датами.
     */
    @Override
    public long calculateTotalDays(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Вычисляет количество дней выполнения привычки за указанный период.
     *
     * @param habitCompletionList список выполнений привычки.
     * @param startDate начало периода.
     * @param endDate конец периода.
     * @return количество дней выполнения привычки за указанный период.
     */
    public long calculateCountDaysInPeriod(List<HabitCompletion> habitCompletionList, LocalDate startDate, LocalDate endDate) {
        return habitCompletionList.stream()
                .filter(habitCompletion ->
                        !habitCompletion.getCompletionDate().isBefore(startDate) &&
                                !habitCompletion.getCompletionDate().isAfter(endDate))
                .count();
    }

    /**
     * Генерирует отчет о привычке.
     *
     * @param id идентификатор привычки.
     * @param periodStart начало периода.
     * @param periodEnd конец периода.
     * @return объект BaseResponse, содержащий отчет о привычке.
     * @throws HabitNotFoundException если привычка не найдена.
     */
    @Override
    public BaseResponse<HabitReportResponse> generateHabitReport(Long id, LocalDate periodStart, LocalDate periodEnd) {
        Habit habit = findHabitById(id);
        long currentStreak = calculateCurrentStreak(id, LocalDate.now()).data();
        long totalCompletions = calculateCountDaysInPeriod(habitCompletionRepository.findByHabitId(id), periodStart, periodEnd);
        double successRate = calculateCompletionPercentage(id, periodStart, periodEnd).data();

        HabitReportResponse report = new HabitReportResponse(habit.getName(), currentStreak, totalCompletions, successRate);

        return new BaseResponse<>("Habit report generated successfully.", report);
    }

    /**
     * Находит выполнения привычки за указанный период.
     *
     * @param habitId идентификатор привычки.
     * @param startDate начало периода.
     * @param endDate конец периода.
     * @return список выполнений привычки за указанный период.
     */
    private List<HabitCompletion> findHabitCompletionsOfPeriod(Long habitId, LocalDate startDate, LocalDate endDate) {
        return habitCompletionRepository.findByHabitId(habitId).stream()
                .filter(habitCompletion ->
                        !habitCompletion.getCompletionDate().isBefore(startDate) &&
                                !habitCompletion.getCompletionDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
}
