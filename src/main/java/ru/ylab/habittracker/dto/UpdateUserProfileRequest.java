package ru.ylab.habittracker.dto;

public record UpdateUserProfileRequest(Long id, String name, String email, String password) {

    @Override
    public String toString() {
        return "UpdateUserProfileRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
