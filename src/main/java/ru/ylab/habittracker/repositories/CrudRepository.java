package ru.ylab.habittracker.repositories;

public interface CrudRepository<T> {
    void save(T entity);
}
