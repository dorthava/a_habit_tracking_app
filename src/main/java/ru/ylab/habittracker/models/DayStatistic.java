package ru.ylab.habittracker.models;

import java.time.LocalDate;

public class DayStatistic {
    private LocalDate date;
    private boolean completed;

    public DayStatistic(LocalDate date, boolean completed) {
        this.date = date;
        this.completed = completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
