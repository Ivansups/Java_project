package com.library.testutil;

import com.library.auth.UserManager;
import com.library.database.DatabaseManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Вспомогательный класс для изолированных тестовых БД.
 */
public final class DatabaseTestHelper {

    private DatabaseTestHelper() {
    }

    /**
     * Создает временную SQLite-БД, сбрасывает синглтоны и возвращает готовый DatabaseManager.
     */
    public static DatabaseManager setupTestDatabase() {
        try {
            Path tempDir = Files.createTempDirectory("library-db-");
            Path dbFile = tempDir.resolve("library.db");
            String jdbcUrl = "jdbc:sqlite:" + dbFile.toAbsolutePath();
            DatabaseManager.useCustomUrlForTests(jdbcUrl);
            UserManager.resetForTests();
            return DatabaseManager.getInstance();
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось создать временную БД для тестов", e);
        }
    }
}

