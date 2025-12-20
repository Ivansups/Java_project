package com.library.gui;

import com.library.auth.UserManager;
import com.library.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserManager userManager;

    public LoginWindow() {
        userManager = UserManager.getInstance();
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        setTitle("Библиотечная система - Вход");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        UITheme.applySberTheme(this);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Войти");

        UITheme.styleTextField(usernameField);
        UITheme.stylePasswordField(passwordField);
        UITheme.styleButton(loginButton);
        loginButton.setOpaque(true);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(40, 40, 40, 40));

        // Заголовок
        JLabel titleLabel = new JLabel("Библиотечная система", SwingConstants.CENTER);
        UITheme.styleTitleLabel(titleLabel);
        titleLabel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 30, 0));

        // Центральная панель с формой
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BACKGROUND_WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(UITheme.BORDER_COLOR, 1, true),
            new javax.swing.border.EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Логин
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Логин:");
        UITheme.styleLabel(usernameLabel);
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        // Пароль
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Пароль:");
        UITheme.styleLabel(passwordLabel);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        // Кнопка входа
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(loginButton, gbc);

        // Информация для гостя
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel guestLabel = new JLabel("<html><center>Для просмотра каталога без авторизации<br/>нажмите 'Просмотр каталога'</center></html>", SwingConstants.CENTER);
        guestLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        guestLabel.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(guestLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JButton guestButton = new JButton("Просмотр каталога");
        UITheme.styleButton(guestButton);
        guestButton.setBackground(UITheme.TEXT_SECONDARY);
        guestButton.setOpaque(true);
        guestButton.addActionListener(e -> {
            dispose();
            new GuestCatalogWindow().setVisible(true);
        });
        formPanel.add(guestButton, gbc);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void setupListeners() {
        ActionListener loginAction = e -> performLogin();

        loginButton.addActionListener(loginAction);

        // Enter для входа
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };

        usernameField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UITheme.showErrorMessage(this, "Пожалуйста, введите логин и пароль");
            return;
        }

        if (userManager.authenticate(username, password)) {
            String role = userManager.getUserRole(username);
            dispose();

            switch (role) {
                case "ADMIN":
                    new AdminWindow(username).setVisible(true);
                    break;
                case "LIBRARIAN":
                    new LibrarianWindow(username).setVisible(true);
                    break;
                case "READER":
                    new ReaderWindow(username).setVisible(true);
                    break;
                default:
                    UITheme.showErrorMessage(this, "Неизвестная роль пользователя");
                    setVisible(true);
            }
        } else {
            UITheme.showErrorMessage(this, "Неверный логин или пароль");
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}

