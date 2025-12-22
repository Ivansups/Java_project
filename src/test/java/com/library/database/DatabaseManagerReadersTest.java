package com.library.database;

import com.library.model.Reader;
import com.library.testutil.DatabaseTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseManagerReadersTest {

    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() {
        dbManager = DatabaseTestHelper.setupTestDatabase();
    }

    @Test
    void shouldAddReaderAndFindByCardNumber() {
        Reader reader = new Reader("Тестовый Читатель", "CARD-100", "+70000000000", "test@reader.ru", "Адрес");

        dbManager.addReader(reader);
        Reader loaded = dbManager.getReaderByCardNumber("CARD-100");

        assertThat(loaded).isNotNull();
        assertThat(loaded.getFullName()).isEqualTo("Тестовый Читатель");
        assertThat(loaded.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldUpdateReaderStatusAndContacts() {
        Reader reader = new Reader("Второй Читатель", "CARD-200", "+70000000001", "second@reader.ru", "Адрес 2");
        dbManager.addReader(reader);
        Reader persisted = dbManager.getReaderByCardNumber("CARD-200");

        persisted.setStatus("BLOCKED");
        persisted.setPhone("+70000000002");
        persisted.setEmail("updated@reader.ru");
        dbManager.updateReader(persisted);

        Reader updated = dbManager.getReaderById(persisted.getId());
        assertThat(updated.getStatus()).isEqualTo("BLOCKED");
        assertThat(updated.getPhone()).isEqualTo("+70000000002");
        assertThat(updated.getEmail()).isEqualTo("updated@reader.ru");
    }
}

