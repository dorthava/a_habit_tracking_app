package ru.ylab.habittracker.utils;

public class StringValidator {
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
