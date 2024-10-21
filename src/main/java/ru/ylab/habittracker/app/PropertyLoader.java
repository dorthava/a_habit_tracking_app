package ru.ylab.habittracker.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для загрузки свойств из файла конфигурации.
 */
public class PropertyLoader {
    private final static Properties properties = new Properties();

    static {
        try (InputStream input = PropertyLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IllegalStateException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    /**
     * Получает значение свойства по ключу.
     *
     * @param key ключ свойства
     * @return значение свойства, или null, если ключ не найден
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
