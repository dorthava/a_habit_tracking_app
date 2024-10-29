package ru.ylab.habittracker.dto;

/**
 * Класс-обертка для возврата ответов сервиса с определенным статусом и данными.
 *
 * @param <T> Тип данных, которые содержатся в ответе.
 */
public record BaseResponse<T>(String status, T data) {

    /**
     * Возвращает строковое представление объекта BaseResponse.
     *
     * @return строка, представляющая объект BaseResponse, содержащая статус и данные.
     */
    @Override
    public String toString() {
        return "BaseResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
