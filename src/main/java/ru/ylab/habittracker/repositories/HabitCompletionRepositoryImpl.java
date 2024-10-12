package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.utils.IdsGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class HabitCompletionRepositoryImpl implements HabitCompletionRepository {
    private final HashMap<Long, HabitCompletion> dataSource;

    public HabitCompletionRepositoryImpl() {
        dataSource = new HashMap<>();
    }

    @Override
    public List<HabitCompletion> findAll() {
        return dataSource.values().stream().toList();
    }

    @Override
    public HabitCompletion save(HabitCompletion entity) {
        Long id = IdsGenerator.getInstance().generateId();
        entity.setId(id);
        dataSource.put(id, entity);
        return entity;
    }

    @Override
    public HabitCompletion update(HabitCompletion entity) {
        HabitCompletion habitCompletion = dataSource.get(entity.getId());
        habitCompletion.setCompletionDate(entity.getCompletionDate());
        return habitCompletion;
    }

    @Override
    public void delete(Long id) {
        dataSource.remove(id);
    }

    @Override
    public Optional<HabitCompletion> findById(Long id) {
        return Optional.ofNullable(dataSource.get(id));
    }
}
