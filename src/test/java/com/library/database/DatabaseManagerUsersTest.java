package com.library.database;

import com.library.testutil.DatabaseTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseManagerUsersTest {

    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() {
        dbManager = DatabaseTestHelper.setupTestDatabase();
    }

    @Test
    void authenticateReturnsTrueForDefaultUser() {
        assertThat(dbManager.authenticateUser("admin", "admin")).isTrue();
    }

    @Test
    void shouldAddAndListSystemUser() {
        dbManager.addSystemUser("new_user", "pwd", "READER");

        assertThat(dbManager.getAllSystemUsers())
            .anySatisfy(user -> {
                assertThat(user.getUsername()).isEqualTo("new_user");
                assertThat(user.getRole()).isEqualTo("READER");
            });
    }

    @Test
    void updateSystemUserChangesPasswordAndRole() {
        dbManager.addSystemUser("change_me", "old", "READER");

        dbManager.updateSystemUser("change_me", "newPwd", "ADMIN");

        assertThat(dbManager.authenticateUser("change_me", "newPwd")).isTrue();
        assertThat(dbManager.getUserRole("change_me")).isEqualTo("ADMIN");
    }

    @Test
    void systemUserExistsBecomesFalseAfterDelete() {
        dbManager.addSystemUser("delete_me", "pwd", "READER");
        assertThat(dbManager.systemUserExists("delete_me")).isTrue();

        dbManager.deleteSystemUser("delete_me");

        assertThat(dbManager.systemUserExists("delete_me")).isFalse();
    }
}

