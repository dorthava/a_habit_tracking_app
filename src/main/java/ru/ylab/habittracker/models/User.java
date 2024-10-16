package ru.ylab.habittracker.models;

import ru.ylab.habittracker.utils.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private final List<Habit> habits;
    private boolean isBlocked;

    public User(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        habits = new ArrayList<>();
        isBlocked = false;
        role = Role.USER;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Habit> getHabits() {
        return habits;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isBlocked == user.isBlocked && Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && role == user.role && Objects.equals(habits, user.habits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, role, habits, isBlocked);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", habits=" + habits +
                ", isBlocked=" + isBlocked +
                '}';
    }
}
