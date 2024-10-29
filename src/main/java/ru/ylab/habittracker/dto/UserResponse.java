package ru.ylab.habittracker.dto;

public record UserResponse(Long id, String name, String email) {

    @Override
    public String toString() {
        return "SignUpResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
