package ru.ylab.habittracker.utils;

public class IdsGenerator {
    private final static IdsGenerator instance;
    private long id;

    static {
        instance = new IdsGenerator();
    }

    private IdsGenerator() {
        id = 0;
    }

    public static IdsGenerator getInstance() {
        return instance;
    }

    public long generateId() {
        return id++;
    }
}
