package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.HabitReportResponse;
import ru.ylab.habittracker.models.HabitCompletion;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для управления выполнением привычек.
 * Этот интерфейс определяет методы для отметки выполнения привычек, получения истории
 * выполнения и статистики выполнения по различным периодам.
 */
public interface HabitCompletionService {

    /**
     * Отмечает выполнение привычки по ее идентификатору.
     *
     * @param id идентификатор привычки, которую нужно отметить как выполненную.
     * @return HabitCompletion объект, представляющий выполнение привычки.
     */
    HabitCompletion markCompletion(Long id);

    /**
     * Показывает историю выполнения привычки по ее идентификатору.
     *
     * @param id идентификатор привычки.
     * @return BaseResponse<List<HabitCompletion>> объект ответа с историей выполнения привычки.
     */
    BaseResponse<List<HabitCompletion>> showTheHistory(Long id);

    /**
     * Получает выполнение привычки за определенный период.
     *
     * @param id идентификатор привычки.
     * @param period строка, представляющая период выполнения.
     * @return BaseResponse<List<HabitCompletion>> объект ответа с выполнением привычки за период.
     */
    BaseResponse<List<HabitCompletion>> getCompletion(Long id, String period);

    /**
     * Получает выполнение привычки за месяц по заданной дате.
     *
     * @param habitId идентификатор привычки.
     * @param localDate дата, с которой начинается месяц.
     * @return BaseResponse<List<HabitCompletion>> объект ответа с выполнением привычки за месяц.
     */
    BaseResponse<List<HabitCompletion>> getCompletionForMonth(Long habitId, LocalDate localDate);

    /**
     * Получает выполнение привычки за неделю по заданной дате.
     *
     * @param habitId идентификатор привычки.
     * @param localDate дата, с которой начинается неделя.
     * @return BaseResponse<List<HabitCompletion>> объект ответа с выполнением привычки за неделю.
     */
    BaseResponse<List<HabitCompletion>> getCompletionForWeek(Long habitId, LocalDate localDate);

    /**
     * Получает выполнение привычки за день по заданной дате.
     *
     * @param habitId идентификатор привычки.
     * @param localDate дата, для которой нужно получить выполнение.
     * @return BaseResponse<List<HabitCompletion>> объект ответа с выполнением привычки за день.
     */
    BaseResponse<List<HabitCompletion>> getCompletionForDay(Long habitId, LocalDate localDate);

    /**
     * Вычисляет текущую серию выполнения привычки.
     *
     * @param id идентификатор привычки.
     * @param currentDate текущая дата для вычисления серии.
     * @return BaseResponse<Integer> объект ответа с количеством дней текущей серии выполнения.
     */
    BaseResponse<Integer> calculateCurrentStreak(Long id, LocalDate currentDate);

    /**
     * Вычисляет процент выполнения привычки за указанный период.
     *
     * @param id идентификатор привычки.
     * @param periodStart начало периода.
     * @param periodEnd конец периода.
     * @return BaseResponse<Double> объект ответа с процентом выполнения привычки.
     */
    BaseResponse<Double> calculateCompletionPercentage(Long id, LocalDate periodStart, LocalDate periodEnd);

    /**
     * Вычисляет общее количество дней между двумя датами.
     *
     * @param startDate начальная дата.
     * @param endDate конечная дата.
     * @return общее количество дней между указанными датами.
     */
    long calculateTotalDays(LocalDate startDate, LocalDate endDate);

    /**
     * Генерирует отчет о выполнении привычки за указанный период.
     *
     * @param id идентификатор привычки.
     * @param periodStart начало периода.
     * @param periodEnd конец периода.
     * @return BaseResponse<HabitReportResponse> объект ответа с отчетом о выполнении привычки.
     */
    BaseResponse<HabitReportResponse> generateHabitReport(Long id, LocalDate periodStart, LocalDate periodEnd);
}
