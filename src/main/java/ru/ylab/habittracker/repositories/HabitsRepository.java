package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.Habit;

import java.util.Optional;

public interface HabitsRepository extends CrudRepository<Habit> {
    Optional<Habit> findById(Long id);
    void deleteById(Long id);
}
