package ru.ylab.habittracker.models;

import ru.ylab.habittracker.utils.Role;

import java.util.Objects;

public class Users {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean isBlocked;

    public Users(Long id, String name, String email, String password, Role role, boolean isBlocked) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isBlocked = isBlocked;
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
        Users users = (Users) o;
        return isBlocked == users.isBlocked && Objects.equals(id, users.id) && Objects.equals(name, users.name) && Objects.equals(email, users.email) && Objects.equals(password, users.password) && role == users.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, role, isBlocked);
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", isBlocked=" + isBlocked +
                '}';
    }
}
