package ru.ylab.habittracker.repositories;

import java.util.List;

/**
 * Интерфейс для выполнения операций CRUD (создание, чтение, обновление, удаление)
 * над сущностями типа {@code T}.
 *
 * @param <T> тип сущности, с которой будет работать репозиторий
 */
public interface CrudRepository<T> {

    /**
     * Находит и возвращает все сущности.
     *
     * @return список всех сущностей
     */
    List<T> findAll();

    /**
     * Сохраняет новую сущность.
     *
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    T save(T entity);

    /**
     * Обновляет существующую сущность.
     *
     * @param entity сущность с обновленными данными
     * @return обновленная сущность
     */
    T update(T entity);

    /**
     * Удаляет сущность по ее идентификатору.
     *
     * @param id идентификатор сущности для удаления
     */
    void delete(Long id);
}
