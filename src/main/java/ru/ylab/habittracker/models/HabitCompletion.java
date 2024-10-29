package ru.ylab.habittracker.models;

import java.time.LocalDate;
import java.util.Objects;

public class HabitCompletion {
    private Long id;
    private LocalDate completionDate;
    private Long habitId;

    public HabitCompletion(Long id, LocalDate completionDate, Long habitId) {
        this.id = id;
        this.completionDate = completionDate;
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

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitCompletion that = (HabitCompletion) o;
        return Objects.equals(id, that.id) && Objects.equals(completionDate, that.completionDate) && Objects.equals(habitId, that.habitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, completionDate, habitId);
    }

    @Override
    public String toString() {
        return "HabitCompletion{" +
                "id=" + id +
                ", completionDate=" + completionDate +
                ", habitId=" + habitId +
                '}';
    }
}
