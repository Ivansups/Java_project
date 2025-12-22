package com.library.gui;

import com.library.testutil.DatabaseTestHelper;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.finder.WindowFinder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;

import static org.assertj.core.api.Assertions.assertThat;

class LoginWindowTest {

    private FrameFixture window;

    @BeforeAll
    static void installRepaintManager() {
        System.setProperty("java.awt.headless", "false");
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    void setUp() {
        DatabaseTestHelper.setupTestDatabase();
        LoginWindow frame = GuiActionRunner.execute(LoginWindow::new);
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
    void emptyFieldsShowErrorDialog() {
        window.button("loginButton").click();

        window.optionPane()
            .requireErrorMessage()
            .requireMessage("Пожалуйста, введите логин и пароль")
            .okButton().click();
    }

    @Test
    void invalidCredentialsShowErrorAndClearPassword() {
        window.textBox("usernameField").setText("unknown");
        window.textBox("passwordField").setText("wrong");

        window.button("loginButton").click();

        window.optionPane()
            .requireErrorMessage()
            .requireMessage("Неверный логин или пароль")
            .okButton().click();
        assertThat(window.textBox("passwordField").text()).isEmpty();
    }

    @Test
    void pressingEnterTriggersLoginAction() {
        window.textBox("usernameField").setText("unknown");
        window.textBox("passwordField").setText("wrong");

        window.textBox("passwordField").pressAndReleaseKeys(KeyEvent.VK_ENTER);

        window.optionPane()
            .requireErrorMessage()
            .requireMessage("Неверный логин или пароль")
            .okButton().click();
    }

    @Test
    void guestButtonOpensGuestCatalogWindow() {
        window.button("guestButton").click();

        FrameFixture guest = WindowFinder.findFrame(GuestCatalogWindow.class).using(window.robot());
        guest.requireVisible();
        guest.cleanUp();
    }

    @Test
    void validCredentialsWithSpacesAuthenticateAndCloseLogin() {
        window.textBox("usernameField").setText("  admin  ");
        window.textBox("passwordField").setText("admin");

        window.button("loginButton").click();

        FrameFixture admin = WindowFinder.findFrame(AdminWindow.class).using(window.robot());
        admin.requireVisible();
        admin.cleanUp();
    }

    @Test
    void pressingEnterInUsernameFieldAlsoTriggersLogin() {
        window.textBox("usernameField").setText("unknown");
        window.textBox("passwordField").setText("wrong");

        window.textBox("usernameField").pressAndReleaseKeys(KeyEvent.VK_ENTER);

        window.optionPane()
            .requireErrorMessage()
            .requireMessage("Неверный логин или пароль")
            .okButton().click();
    }
}

