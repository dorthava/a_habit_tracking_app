package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.models.Habit;

import java.time.LocalDate;
import java.util.List;

public interface HabitsService {

    /**
     * Создает новую привычку.
     *
     * @param habit объект Habit, который нужно создать.
     * @return BaseResponse<Habit> объект ответа с созданной привычкой.
     */
    BaseResponse<Habit> create(Habit habit);

    /**
     * Обновляет существующую привычку.
     *
     * @param habit объект Habit с обновленными данными.
     * @return BaseResponse<Habit> объект ответа с обновленной привычкой.
     */
    BaseResponse<Habit> update(Habit habit);

    /**
     * Удаляет привычку по ее идентификатору.
     *
     * @param id идентификатор привычки, которую нужно удалить.
     */
    void delete(Long id);

    /**
     * Находит привычки пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя, чьи привычки нужно найти.
     * @return BaseResponse<List < Habit>> объект ответа со списком привычек пользователя.
     */
    BaseResponse<List<Habit>> findByUserId(Long userId);

    /**
     * Находит привычки пользователя по его идентификатору и дате создания.
     *
     * @param userId    идентификатор пользователя, чьи привычки нужно найти.
     * @param localDate дата создания привычки.
     * @return BaseResponse<List < Habit>> объект ответа со списком привычек пользователя за указанную дату.
     */
    BaseResponse<List<Habit>> findByUserIdAndDate(Long userId, LocalDate localDate);
}
