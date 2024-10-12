package ru.ylab.habittracker.utils;

public enum Frequency {
    DAILY("daily"),
    WEEKLY("weekly");

    private final String label;

    Frequency(String label) {
        this.label = label;
    }

    public static Frequency fromString(String label) {
        for (Frequency frequency : Frequency.values()) {
            if (frequency.label.equalsIgnoreCase(label)) {
                return frequency;
            }
        }
        throw new IllegalArgumentException("Unknown frequency: " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}