package ru.ylab.habittracker.utils;

/**
 * Перечисление частоты выполнения действий с соответствующими числовыми значениями.
 */
public enum Frequency {
    DAILY(0),
    WEEKLY(1);

    private final int value;

    /**
     * Конструктор для установки числового значения частоты.
     *
     * @param value числовое значение частоты
     */
    Frequency(int value) {
        this.value = value;
    }

    /**
     * Возвращает частоту, соответствующую переданному значению.
     *
     * @param value числовое значение частоты
     * @return соответствующая частота
     * @throws IllegalArgumentException если значение не соответствует ни одной частоте
     */
    public static Frequency fromValue(int value) {
        for (Frequency frequency : values()) {
            if (frequency.value == value) {
                return frequency;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}