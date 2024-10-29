package ru.ylab.habittracker.services.impl;

import ru.ylab.habittracker.dto.BaseResponse;
import ru.ylab.habittracker.dto.UpdateUserProfileRequest;
import ru.ylab.habittracker.dto.UserResponse;
import ru.ylab.habittracker.models.Users;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.services.UsersService;
import ru.ylab.habittracker.utils.Role;

import java.util.Optional;

/**
 * Реализация сервиса для управления пользователями.
 * Этот класс отвечает за операции, связанные с пользователями, такие как создание, обновление,
 * удаление и блокировка пользователей.
 */
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    /**
     * Конструктор класса UsersServiceImpl.
     *
     * @param usersRepository репозиторий для доступа к данным пользователей.
     */
    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * Создает нового пользователя.
     *
     * @param users объект пользователя, который необходимо создать.
     * @return Users созданный пользователь.
     */
    @Override
    public Users create(Users users) {
        return usersRepository.save(users);
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param updateUserProfileRequest объект запроса с новыми данными пользователя.
     * @return BaseResponse<UserResponse> объект ответа, содержащий обновленную информацию о пользователе.
     * @throws RuntimeException если пользователь не найден.
     */
    @Override
    public BaseResponse<UserResponse> update(UpdateUserProfileRequest updateUserProfileRequest) {
        Optional<Users> optionalUser = usersRepository.findById(updateUserProfileRequest.id());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Users user = optionalUser.get();
        user.setName(updateUserProfileRequest.name());
        user.setEmail(updateUserProfileRequest.email());
        user.setPassword(updateUserProfileRequest.password());
        user = usersRepository.update(user);

        return new BaseResponse<>("User updated", new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого необходимо удалить.
     */
    @Override
    public void delete(Long id) {
        usersRepository.delete(id);
    }

    /**
     * Блокирует пользователя по его идентификатору, если администратор имеет права.
     *
     * @param adminId идентификатор администратора, выполняющего блокировку.
     * @param userId идентификатор пользователя, которого необходимо заблокировать.
     * @return BaseResponse<Void> объект ответа с информацией о статусе блокировки.
     * @throws RuntimeException если администратор или пользователь не найдены, или если
     * администратор не имеет прав.
     */
    @Override
    public BaseResponse<Void> blockUser(Long adminId, Long userId) {
        Optional<Users> optionalAdmin = usersRepository.findById(adminId);
        if (optionalAdmin.isEmpty()) {
            throw new RuntimeException("Admin not found");
        }

        if (optionalAdmin.get().getRole() == Role.USER) {
            throw new RuntimeException("Forbidden");
        }

        Optional<Users> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Users users = optionalUser.get();
        users.setBlocked(true);
        usersRepository.update(users);
        return new BaseResponse<>("User blocked", null);
    }

    /**
     * Удаляет пользователя по его идентификатору, если администратор имеет права.
     *
     * @param adminId идентификатор администратора, выполняющего удаление.
     * @param userId идентификатор пользователя, которого необходимо удалить.
     * @return BaseResponse<Void> объект ответа с информацией о статусе удаления.
     * @throws RuntimeException если администратор или пользователь не найдены, или если
     * администратор не имеет прав.
     */
    @Override
    public BaseResponse<Void> deleteUserByAdmin(Long adminId, Long userId) {
        Optional<Users> optionalAdmin = usersRepository.findById(adminId);
        Optional<Users> optionalUser = usersRepository.findById(userId);
        if (optionalAdmin.isEmpty() || optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        if (optionalAdmin.get().getRole() == Role.USER) {
            throw new RuntimeException("Forbidden");
        }
        usersRepository.delete(userId);
        return new BaseResponse<>("User deleted", null);
    }

    /**
     * Устанавливает роль администратора для пользователя по его email.
     *
     * @param email email пользователя, которому необходимо назначить роль администратора.
     * @throws RuntimeException если пользователь не найден.
     */
    @Override
    public void setAdminRole(String email) {
        Optional<Users> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Users users = optionalUser.get();
        users.setRole(Role.ADMIN);
        usersRepository.update(users);
    }
}