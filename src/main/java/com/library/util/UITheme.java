package com.library.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UITheme {
    // Цветовая палитра Сбера
    public static final Color PRIMARY_GREEN = new Color(0, 168, 107); // #00A86B
    public static final Color PRIMARY_GREEN_DARK = new Color(0, 140, 90);
    public static final Color PRIMARY_GREEN_LIGHT = new Color(0, 193, 122); // #00C17A
    public static final Color BACKGROUND_LIGHT = new Color(248, 255, 252);
    public static final Color BACKGROUND_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_PRIMARY = new Color(30, 30, 30);
    public static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    public static final Color BORDER_COLOR = new Color(230, 230, 230);
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    public static final Color ERROR_COLOR = new Color(244, 67, 54);
    public static final Color WARNING_COLOR = new Color(255, 152, 0);

    // Градиенты
    public static GradientPaint getPrimaryGradient(Rectangle bounds) {
        return new GradientPaint(
            0, 0, PRIMARY_GREEN,
            0, bounds.height, PRIMARY_GREEN_DARK
        );
    }

    public static GradientPaint getLightGradient(Rectangle bounds) {
        return new GradientPaint(
            0, 0, BACKGROUND_LIGHT,
            0, bounds.height, BACKGROUND_WHITE
        );
    }

    // Стилизация компонентов
    public static void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_GREEN);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Math.max(button.getPreferredSize().width, 150), 45));
        button.setBorder(new EmptyBorder(12, 25, 12, 25));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_GREEN_LIGHT);
                button.setOpaque(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_GREEN);
                button.setOpaque(true);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_GREEN_DARK);
                button.setOpaque(true);
            }
        });
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        textField.setBackground(BACKGROUND_WHITE);
        textField.setForeground(TEXT_PRIMARY);
        textField.setPreferredSize(new Dimension(Math.max(textField.getPreferredSize().width, 250), 45));
    }

    public static void stylePasswordField(JPasswordField passwordField) {
        styleTextField(passwordField);
    }

    public static void styleTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        textArea.setBackground(BACKGROUND_WHITE);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    public static void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
    }

    public static void styleTitleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(PRIMARY_GREEN);
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionBackground(PRIMARY_GREEN_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        if (table.getTableHeader() != null) {
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.getTableHeader().setBackground(PRIMARY_GREEN);
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setReorderingAllowed(false);
        }
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setBackground(BACKGROUND_WHITE);
        comboBox.setPreferredSize(new Dimension(Math.max(comboBox.getPreferredSize().width, 250), 45));
    }

    public static JPanel createRoundedPanel(Color bgColor, int arc) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
                g2.dispose();
            }
        };
    }

    public static void applySberTheme(JFrame frame) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame.getContentPane().setBackground(BACKGROUND_LIGHT);
    }

    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Успешно",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Ошибка",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static int showConfirmDialog(Component parent, String message) {
        return JOptionPane.showConfirmDialog(
            parent,
            message,
            "Подтверждение",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
    }
}

