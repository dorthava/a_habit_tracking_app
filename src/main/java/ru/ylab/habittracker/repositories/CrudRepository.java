package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.User;

import java.util.List;

public interface CrudRepository<T> {

    List<T> findAll();

    T save(T entity);

    T update(T entity);

    void delete(Long id);
}
