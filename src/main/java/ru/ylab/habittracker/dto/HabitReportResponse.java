package ru.ylab.habittracker.dto;

public class HabitReportResponse {
    private String habitName;
    private long currentStreak;
    private long totalCompletions;
    private double successRate;

    public HabitReportResponse(String habitName, long currentStreak, long totalCompletions, double successRate) {
        this.habitName = habitName;
        this.currentStreak = currentStreak;
        this.totalCompletions = totalCompletions;
        this.successRate = successRate;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public long getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(long currentStreak) {
        this.currentStreak = currentStreak;
    }

    public long getTotalCompletions() {
        return totalCompletions;
    }

    public void setTotalCompletions(long totalCompletions) {
        this.totalCompletions = totalCompletions;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    @Override
    public String toString() {
        return "HabitReportResponse{" +
                "habitName='" + habitName + '\'' +
                ", currentStreak=" + currentStreak +
                ", totalCompletions=" + totalCompletions +
                ", successRate=" + successRate +
                '}';
    }
}
