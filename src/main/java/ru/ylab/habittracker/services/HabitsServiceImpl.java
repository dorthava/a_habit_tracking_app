package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.UsersRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HabitsServiceImpl implements HabitsService {
    private final UsersRepository usersRepository;
    private final HabitsRepository habitsRepository;

    public HabitsServiceImpl(UsersRepository usersRepository, HabitsRepository habitsRepository) {
        this.usersRepository = usersRepository;
        this.habitsRepository = habitsRepository;
    }

    @Override
    public BaseResponse<Habit> create(Habit habit) {
        Optional<User> optionalUser = usersRepository.findByEmail(habit.getCreatedBy());
        if(optionalUser.isEmpty()) {
             return new BaseResponse<>(false, "The user was not found.", null);
        }
        User user = optionalUser.get();
        habit = habitsRepository.save(habit);
        user.getHabits().add(habit);
        return new BaseResponse<>(true, "The habit was successfully created.", habit);
    }

    @Override
    public BaseResponse<Habit> update(Habit habit) {
        Optional<User> optionalUser = usersRepository.findByEmail(habit.getCreatedBy());
        if(optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "The user was not found.", null);
        }
        Optional<Habit> optionalHabit = habitsRepository.findById(habit.getId());
        if(optionalHabit.isEmpty()) {
            return new BaseResponse<>(false, "The habit was not found.", null);
        }
        Habit result = habitsRepository.update(habit);
        return new BaseResponse<>(true, "The habit was successfully updated.", result);
    }

    @Override
    public void delete(Long id) {
        Optional<Habit> optionalHabit = habitsRepository.findById(id);
        if(optionalHabit.isEmpty()) {
            return;
        }
        Habit habit = optionalHabit.get();
        Optional<User> optionalUser = usersRepository.findByEmail(habit.getCreatedBy());
        if(optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        user.getHabits().remove(habit);
        habitsRepository.deleteById(id);
    }

    @Override
    public BaseResponse<List<Habit>> findByEmail(String email) {
        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "The user was not found.", null);
        }
        User user = optionalUser.get();
        List<Habit> habits = user.getHabits();
        return new BaseResponse<>(true, "The habits found.", habits);
    }

    @Override
    public BaseResponse<List<Habit>> findByEmailAndDate(String email, LocalDate localDate) {
        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return new BaseResponse<>(false, "The user was not found.", null);
        }
        User user = optionalUser.get();
        List<Habit> habits = user.getHabits().stream()
                .filter(habit -> localDate.equals(habit.getCreatedDate())).toList();
        return new BaseResponse<>(true, "The habits found.", habits);
    }
}
