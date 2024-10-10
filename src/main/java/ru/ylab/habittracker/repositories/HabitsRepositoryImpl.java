package ru.ylab.habittracker.repositories;

import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.utils.IdsGenerator;
import ru.ylab.habittracker.utils.StringValidator;

import java.time.LocalDate;
import java.util.*;

public class HabitsRepositoryImpl implements HabitsRepository {
    private final HashMap<Long, Habit> dataSource;

    public HabitsRepositoryImpl() {
        this.dataSource = new HashMap<>();
    }

    @Override
    public List<Habit> findAll() {
        return dataSource.values().stream().toList();
    }

    @Override
    public Habit save(Habit entity) {
        Long generatedId = IdsGenerator.getInstance().generateId();
        entity.setId(generatedId);
        dataSource.put(generatedId, entity);
        return entity;
    }

    @Override
    public Habit update(Habit entity) {
        Habit habit = dataSource.get(entity.getId());
        if(StringValidator.isValidString(entity.getName())) {
            habit.setName(entity.getName());
        }
        if(StringValidator.isValidString(entity.getDescription())) {
            habit.setDescription(entity.getDescription());
        }
        habit.setCreatedDate(LocalDate.now());
        return habit;
    }

    @Override
    public void delete(String email) {
        dataSource.entrySet().removeIf(entry -> entry.getValue().getCreatedBy().equals(email));
    }

    @Override
    public Optional<Habit> findById(Long id) {
        return Optional.ofNullable(dataSource.get(id));
    }

    @Override
    public void deleteById(Long id) {
        dataSource.remove(id);
    }
}
