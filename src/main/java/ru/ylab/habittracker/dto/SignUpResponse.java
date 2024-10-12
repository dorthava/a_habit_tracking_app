package ru.ylab.habittracker.dto;

public class SignUpResponse {
    private final boolean success;
    private final String message;
    private String email;

    public SignUpResponse(boolean success, String message, String email) {
        this.success = success;
        this.message = message;
        this.email = email;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "SignUpResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
