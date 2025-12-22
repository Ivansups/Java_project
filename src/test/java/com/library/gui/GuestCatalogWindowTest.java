package com.library.gui;

import com.library.database.DatabaseManager;
import com.library.testutil.DatabaseTestHelper;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.finder.WindowFinder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuestCatalogWindowTest {

    private FrameFixture window;
    private DatabaseManager dbManager;

    @BeforeAll
    static void install() {
        System.setProperty("java.awt.headless", "false");
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    void setUp() {
        dbManager = DatabaseTestHelper.setupTestDatabase();
        GuestCatalogWindow frame = GuiActionRunner.execute(GuestCatalogWindow::new);
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Test
    void loadsBooksOnStartup() {
        int rows = window.table("booksTable").rowCount();
        assertThat(rows).isGreaterThan(0);
    }

    @Test
    void searchFiltersBooks() {
        int initial = window.table("booksTable").rowCount();

        window.textBox("searchField").setText("Толстой");
        window.button("searchButton").click();

        int afterSearch = window.table("booksTable").rowCount();
        assertThat(afterSearch).isPositive();
        assertThat(afterSearch).isLessThanOrEqualTo(initial);
    }

    @Test
    void showAllRestoresRows() {
        int initial = window.table("booksTable").rowCount();
        window.textBox("searchField").setText("не_существует");
        window.button("searchButton").click();
        assertThat(window.table("booksTable").rowCount()).isEqualTo(0);

        window.button("showAllButton").click();

        assertThat(window.table("booksTable").rowCount()).isEqualTo(initial);
    }

    @Test
    void loginButtonNavigatesBackToLoginWindow() {
        window.button("loginNavButton").click();

        FrameFixture login = WindowFinder.findFrame(LoginWindow.class).using(window.robot());
        login.requireVisible();
        login.cleanUp();
    }
}

