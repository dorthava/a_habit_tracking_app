package ru.ylab.habittracker.services.impl;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.Users;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.services.HabitsService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления привычками пользователей.
 * Этот класс отвечает за операции, связанные с привычками, такие как создание, обновление,
 * удаление и поиск привычек пользователя.
 */
public class HabitsServiceImpl implements HabitsService {
    private final UsersRepository usersRepository;
    private final HabitsRepository habitsRepository;

    /**
     * Конструктор класса HabitsServiceImpl.
     *
     * @param usersRepository репозиторий для доступа к данным пользователей.
     * @param habitsRepository репозиторий для доступа к данным привычек.
     */
    public HabitsServiceImpl(UsersRepository usersRepository, HabitsRepository habitsRepository) {
        this.usersRepository = usersRepository;
        this.habitsRepository = habitsRepository;
    }

    /**
     * Создает новую привычку для пользователя.
     *
     * @param habit объект привычки, который необходимо создать.
     * @return BaseResponse<Habit> объект ответа, содержащий созданную привычку.
     * @throws RuntimeException если пользователь не найден.
     */
    @Override
    public BaseResponse<Habit> create(Habit habit) {
        validateUserExists(habit.getUserId());
        habit = habitsRepository.save(habit);
        return new BaseResponse<>("The habit was successfully created.", habit);
    }

    /**
     * Обновляет информацию о привычке.
     *
     * @param habit объект привычки с обновленной информацией.
     * @return BaseResponse<Habit> объект ответа, содержащий обновленную привычку.
     * @throws RuntimeException если пользователь или привычка не найдены.
     */
    @Override
    public BaseResponse<Habit> update(Habit habit) {
        validateUserExists(habit.getUserId());
        Optional<Habit> optionalHabit = habitsRepository.findById(habit.getId());
        if (optionalHabit.isEmpty()) {
            throw new RuntimeException("The habit was not found.");
        }
        habit = habitsRepository.update(habit);
        return new BaseResponse<>("The habit was successfully updated.", habit);
    }

    /**
     * Удаляет привычку по её идентификатору.
     *
     * @param id идентификатор привычки, которую необходимо удалить.
     */
    @Override
    public void delete(Long id) {
        Optional<Habit> optionalHabit = habitsRepository.findById(id);
        if (optionalHabit.isEmpty()) {
            return;
        }
        Habit habit = optionalHabit.get();
        validateUserExists(habit.getUserId());
        habitsRepository.delete(id);
    }

    /**
     * Находит привычки пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя, чьи привычки нужно найти.
     * @return BaseResponse<List<Habit>> объект ответа, содержащий список привычек пользователя.
     * @throws RuntimeException если пользователь не найден.
     */
    @Override
    public BaseResponse<List<Habit>> findByUserId(Long userId) {
        validateUserExists(userId);
        List<Habit> habits = habitsRepository.findByUserId(userId);
        return new BaseResponse<>("The habits found.", habits);
    }

    /**
     * Находит привычки пользователя по его идентификатору и дате.
     *
     * @param userId идентификатор пользователя, чьи привычки нужно найти.
     * @param localDate дата, по которой необходимо найти привычки.
     * @return BaseResponse<List<Habit>> объект ответа, содержащий список привычек пользователя.
     * @throws RuntimeException если пользователь не найден.
     */
    @Override
    public BaseResponse<List<Habit>> findByUserIdAndDate(Long userId, LocalDate localDate) {
        validateUserExists(userId);
        List<Habit> habits = habitsRepository.findByUserIdAndDate(userId, localDate);
        return new BaseResponse<>("The habits found.", habits);
    }

    /**
     * Проверяет существование пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя, которого необходимо проверить.
     * @throws RuntimeException если пользователь не найден.
     */
    private void validateUserExists(Long userId) {
        Optional<Users> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("The user was not found.");
        }
    }
}
