package ru.ylab.habittracker.services;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.UpdateUserProfileRequest;
import ru.ylab.habittracker.dto.UserResponse;
import ru.ylab.habittracker.models.Users;

/**
 * Сервис для управления пользователями.
 * Этот интерфейс определяет методы для создания, обновления, удаления и изменения ролей пользователей.
 */
public interface UsersService {

    /**
     * Создает нового пользователя.
     *
     * @param users объект Users, который нужно создать.
     * @return созданный объект Users.
     */
    Users create(Users users);

    /**
     * Обновляет профиль пользователя.
     *
     * @param updateUserProfileRequest объект, содержащий данные для обновления профиля пользователя.
     * @return BaseResponse<UserResponse> объект ответа с обновленными данными пользователя.
     */
    BaseResponse<UserResponse> update(UpdateUserProfileRequest updateUserProfileRequest);

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно удалить.
     */
    void delete(Long id);

    /**
     * Блокирует пользователя по его идентификатору.
     *
     * @param adminId идентификатор администратора, выполняющего блокировку.
     * @param userId  идентификатор пользователя, которого нужно заблокировать.
     * @return BaseResponse<Void> объект ответа о результате выполнения операции.
     */
    BaseResponse<Void> blockUser(Long adminId, Long userId);

    /**
     * Удаляет пользователя по его идентификатору администратором.
     *
     * @param adminId идентификатор администратора, выполняющего удаление.
     * @param userId  идентификатор пользователя, которого нужно удалить.
     * @return BaseResponse<Void> объект ответа о результате выполнения операции.
     */
    BaseResponse<Void> deleteUserByAdmin(Long adminId, Long userId);

    /**
     * Устанавливает роль администратора для пользователя по его электронной почте.
     *
     * @param email адрес электронной почты пользователя, которому нужно установить роль администратора.
     */
    void setAdminRole(String email);
}
