package ru.ylab.habittracker.utils;

/**
 * Перечисление ролей пользователей с соответствующими числовыми значениями.
 */
public enum Role {
    USER(0),
    ADMIN(1);

    private final int value;

    /**
     * Конструктор для установки числового значения роли.
     *
     * @param value числовое значение роли
     */
    Role(int value) {
        this.value = value;
    }

    /**
     * Возвращает роль, соответствующую переданному значению.
     *
     * @param value числовое значение роли
     * @return соответствующая роль
     * @throws IllegalArgumentException если значение не соответствует ни одной роли
     */
    public static Role fromValue(int value) {
        for (Role role : values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
