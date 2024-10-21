package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.Habit;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями {@code Habit}.
 *
 * <p>Расширяет интерфейс {@link CrudRepository} и добавляет методы
 * для выполнения специфичных операций с привычками пользователей.</p>
 */
public interface HabitsRepository extends CrudRepository<Habit> {
    /**
     * Находит привычку по ее идентификатору.
     *
     * @param id идентификатор привычки
     * @return {@link Optional} с привычкой, если найдена, иначе {@link Optional#empty()}
     */
    Optional<Habit> findById(Long id);

    /**
     * Находит все привычки по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список привычек пользователя
     */
    List<Habit> findByUserId(Long userId);

    /**
     * Находит привычки по идентификатору пользователя и дате создания.
     *
     * @param userId идентификатор пользователя
     * @param createdDate дата создания привычки
     * @return список привычек, созданных в указанную дату
     */
    List<Habit> findByUserIdAndDate(Long userId, LocalDate createdDate);
}
