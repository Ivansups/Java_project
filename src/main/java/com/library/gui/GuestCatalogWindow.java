package com.library.gui;

import com.library.database.DatabaseManager;
import com.library.model.Book;
import com.library.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GuestCatalogWindow extends JFrame {
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private DatabaseManager dbManager;

    public GuestCatalogWindow() {
        dbManager = DatabaseManager.getInstance();
        initializeComponents();
        setupLayout();
        loadBooks();
    }

    private void initializeComponents() {
        setTitle("Каталог книг - Гость");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        UITheme.applySberTheme(this);

        String[] columnNames = {"ID", "Название", "Автор", "Год", "Доступно", "Всего"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(tableModel);
        UITheme.styleTable(booksTable);

        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);

        JButton searchButton = new JButton("Поиск");
        UITheme.styleButton(searchButton);
        searchButton.addActionListener(e -> performSearch());

        JButton showAllButton = new JButton("Показать все");
        UITheme.styleButton(showAllButton);
        showAllButton.setBackground(UITheme.TEXT_SECONDARY);
        showAllButton.addActionListener(e -> loadBooks());

        JButton loginButton = new JButton("Войти в систему");
        UITheme.styleButton(loginButton);
        loginButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        // Заголовок
        JLabel titleLabel = new JLabel("Каталог книг", SwingConstants.CENTER);
        UITheme.styleTitleLabel(titleLabel);
        titleLabel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));

        // Панель поиска
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        searchPanel.setOpaque(true);
        searchPanel.add(new JLabel("Поиск:"));
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Поиск");
        UITheme.styleButton(searchBtn);
        searchBtn.setOpaque(true);
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn);
        JButton showAllBtn = new JButton("Показать все");
        UITheme.styleButton(showAllBtn);
        showAllBtn.setBackground(UITheme.TEXT_SECONDARY);
        showAllBtn.setOpaque(true);
        showAllBtn.addActionListener(e -> loadBooks());
        searchPanel.add(showAllBtn);
        searchPanel.add(Box.createHorizontalGlue());
        JButton loginBtn = new JButton("Войти в систему");
        UITheme.styleButton(loginBtn);
        loginBtn.setOpaque(true);
        loginBtn.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });
        searchPanel.add(loginBtn);

        // Таблица
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        List<Book> books = dbManager.getAllBooks();
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getCopiesAvailable(),
                book.getCopiesTotal()
            });
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<Book> books = dbManager.searchBooks(query);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getCopiesAvailable(),
                book.getCopiesTotal()
            });
        }
    }
}

