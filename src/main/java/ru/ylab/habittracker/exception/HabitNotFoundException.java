package ru.ylab.habittracker.exception;

/**
 * Исключение, возникающее при отсутствии привычки.
 */
public class HabitNotFoundException extends RuntimeException {
    /**
     * Конструктор для создания экземпляра HabitNotFoundException.
     *
     * @param message сообщение об ошибке.
     */
    public HabitNotFoundException(String message) {
        super(message);
    }
}