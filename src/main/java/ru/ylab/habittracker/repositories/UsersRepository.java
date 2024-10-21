package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.Users;

import java.util.Optional;


/**
 * Интерфейс репозитория для управления сущностями {@code Users}.
 *
 * <p>Расширяет интерфейс {@link CrudRepository} и добавляет методы
 * для выполнения специфичных операций с пользователями.</p>
 */
public interface UsersRepository extends CrudRepository<Users> {
    /**
     * Находит пользователя по его email.
     *
     * @param email email пользователя
     * @return {@link Optional} с пользователем, если найден, иначе {@link Optional#empty()}
     */
    Optional<Users> findByEmail(String email);

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return {@link Optional} с пользователем, если найден, иначе {@link Optional#empty()}
     */
    Optional<Users> findById(Long id);
}
