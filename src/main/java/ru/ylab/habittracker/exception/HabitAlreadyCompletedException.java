package ru.ylab.habittracker.exception;

/**
 * Исключение, возникающее, когда привычка уже выполнена.
 */
public class HabitAlreadyCompletedException extends RuntimeException {
    /**
     * Конструктор для создания экземпляра HabitAlreadyCompletedException.
     *
     * @param message сообщение об ошибке.
     */
    public HabitAlreadyCompletedException(String message) {
        super(message);
    }
}
