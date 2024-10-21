package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.HabitCompletion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями {@code HabitCompletion}.
 *
 * <p>Расширяет интерфейс {@link CrudRepository} и добавляет методы
 * для выполнения специфичных операций с завершениями привычек.</p>
 */
public interface HabitCompletionRepository extends CrudRepository<HabitCompletion> {
    /**
     * Находит завершение привычки по его идентификатору.
     *
     * @param id идентификатор завершения привычки
     * @return {@link Optional} с завершением привычки, если найдено, иначе {@link Optional#empty()}
     */
    Optional<HabitCompletion> findById(Long id);

    /**
     * Находит все завершения привычки по идентификатору привычки.
     *
     * @param habitId идентификатор привычки
     * @return список завершений привычки
     */
    List<HabitCompletion> findByHabitId(Long habitId);

    /**
     * Находит завершения привычки по идентификатору привычки и диапазону дат.
     *
     * @param habitId идентификатор привычки
     * @param startDate дата начала периода
     * @param endDate дата окончания периода
     * @return список завершений привычки за указанный период
     */
    List<HabitCompletion> findByHabitIdAndPeriod(Long habitId, LocalDate startDate, LocalDate endDate);
}
