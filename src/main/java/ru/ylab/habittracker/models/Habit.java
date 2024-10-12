package ru.ylab.habittracker.models;

import ru.ylab.habittracker.utils.Frequency;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Habit {
    private Long id;
    private String name;
    private String description;
    private Frequency frequency;
    private String createdBy;
    private LocalDate createdDate;
    private List<HabitCompletion> completions;

    public Habit(Long id, String name, String description, Frequency frequency, String createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdBy = createdBy;
        this.createdDate = LocalDate.now();
        completions = new ArrayList<>();
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public List<HabitCompletion> getCompletions() {
        return completions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habit habit = (Habit) o;
        return Objects.equals(id, habit.id) && Objects.equals(name, habit.name) && Objects.equals(description, habit.description)
                && frequency == habit.frequency && Objects.equals(createdBy, habit.createdBy)
                && Objects.equals(createdDate, habit.createdDate)
                && Objects.equals(completions, habit.completions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, frequency, createdBy, createdDate, completions);
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", frequency=" + frequency +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                ", completions=" + completions +
                '}';
    }
}
