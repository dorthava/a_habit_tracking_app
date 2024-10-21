package ru.ylab.habittracker.exception;

/**
 * Исключение, возникающее при неверном периоде.
 */
public class InvalidPeriodException extends RuntimeException {
    /**
     * Конструктор для создания экземпляра InvalidPeriodException.
     *
     * @param message сообщение об ошибке.
     */
    public InvalidPeriodException(String message) {
        super(message);
    }
}