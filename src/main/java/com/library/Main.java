package com.library;

import com.library.database.DatabaseManager;
import com.library.gui.LoginWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Инициализация базы данных
        DatabaseManager.getInstance();

        // Запуск GUI в потоке событий Swing
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}

