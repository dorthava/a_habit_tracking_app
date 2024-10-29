package ru.ylab.habittracker.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс для установления подключения к базе данных с использованием свойств конфигурации.
 */
public class DatabaseConnection {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Получает подключение к базе данных на основе значений, загруженных из файла конфигурации.
     *
     * @return объект {@link Connection}, представляющий соединение с базой данных.
     * @throws SQLException если отсутствуют необходимые свойства для подключения или возникает ошибка при установке соединения.
     */
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
