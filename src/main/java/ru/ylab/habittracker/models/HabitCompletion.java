package ru.ylab.habittracker.models;

import ru.ylab.habittracker.utils.IdsGenerator;

import java.time.LocalDate;
import java.util.Objects;

public class HabitCompletion {
    private Long id;
    private LocalDate completionDate;
    private boolean completed;
    private Long habitId;

    public HabitCompletion(LocalDate completionDate, Long habitId) {
        this.completionDate = completionDate;
        this.habitId = habitId;
    }

    public HabitCompletion(LocalDate completionDate, boolean completed, Long habitId) {
        this.id = IdsGenerator.getInstance().generateId();
        this.completionDate = completionDate;
        this.completed = completed;
        this.habitId = habitId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, completionDate, completed, habitId);
    }

    @Override
    public String toString() {
        return "HabitCompletion{" +
                "id=" + id +
                ", completionDate=" + completionDate +
                ", completed=" + completed +
                ", habitId=" + habitId +
                '}';
    }
}
