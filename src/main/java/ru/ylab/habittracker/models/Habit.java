package ru.ylab.habittracker.models;

import ru.ylab.habittracker.utils.Frequency;

import java.time.LocalDate;
import java.util.Objects;

public class Habit {
    private Long id;
    private String name;
    private String description;
    private Frequency frequency;
    private Long userId;
    private LocalDate createdDate;

    public Habit(Long id, String name, String description, Frequency frequency, Long userId, LocalDate createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.userId = userId;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habit habit = (Habit) o;
        return Objects.equals(id, habit.id) && Objects.equals(name, habit.name) && Objects.equals(description, habit.description) && frequency == habit.frequency && Objects.equals(userId, habit.userId) && Objects.equals(createdDate, habit.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, frequency, userId, createdDate);
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", frequency=" + frequency +
                ", userId=" + userId +
                ", createdDate=" + createdDate +
                '}';
    }
}
