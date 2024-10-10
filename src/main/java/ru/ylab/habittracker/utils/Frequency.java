package ru.ylab.habittracker.utils;

public enum Frequency {
    DAILY("daily"),
    WEEKLY("weekly");

    private final String label;

    Frequency(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}