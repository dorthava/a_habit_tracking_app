package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.HabitCompletion;

import java.util.Optional;

public interface HabitCompletionRepository extends CrudRepository<HabitCompletion> {
    Optional<HabitCompletion> findById(Long id);
}
