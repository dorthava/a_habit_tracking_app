package ru.ylab.habittracker.dto;

public class SignUpResponse {
    private boolean success;
    private String message;
    private Long id;

    public SignUpResponse(boolean success, String message, Long id) {
        this.success = success;
        this.message = message;
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SignInResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", id=" + id +
                '}';
    }
}
