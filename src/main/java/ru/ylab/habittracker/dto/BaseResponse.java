package ru.ylab.habittracker.dto;

public record BaseResponse<T>(boolean success, String message, T data) {

    @Override
    public String toString() {
        return "BaseResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
