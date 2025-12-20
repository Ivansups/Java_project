package com.library.gui;

import com.library.database.DatabaseManager;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Reader;
import com.library.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReaderWindow extends JFrame {
    private String username;
    private DatabaseManager dbManager;
    private Reader currentReader;
    
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JTextField searchField;

    public ReaderWindow(String username) {
        this.username = username;
        this.dbManager = DatabaseManager.getInstance();
        findReaderByUsername();
        initializeComponents();
        setupLayout();
        loadBooks();
        loadHistory();
    }

    private void findReaderByUsername() {
        // Ищем читателя по имени пользователя (упрощенно - можно улучшить)
        List<Reader> readers = dbManager.getAllReaders();
        for (Reader reader : readers) {
            // Предполагаем, что имя пользователя может совпадать с ФИО или номером билета
            if (reader.getCardNumber().equalsIgnoreCase(username) || 
                reader.getFullName().toLowerCase().contains(username.toLowerCase())) {
                currentReader = reader;
                return;
            }
        }
        // Если не нашли, создаем временного читателя
        currentReader = new Reader(username, username, "", "", "");
    }

    private void initializeComponents() {
        setTitle("Библиотечная система - Читатель: " + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        UITheme.applySberTheme(this);

        // Таблица книг
        String[] bookColumns = {"ID", "Название", "Автор", "Год", "Доступно", "Всего"};
        booksTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(booksTableModel);
        UITheme.styleTable(booksTable);

        // Таблица истории
        String[] historyColumns = {"ID", "Книга", "Дата выдачи", "Срок возврата", "Статус"};
        historyTableModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);
        UITheme.styleTable(historyTable);

        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        // Заголовок
        JLabel titleLabel = new JLabel("Кабинет читателя: " + (currentReader != null ? currentReader.getFullName() : username));
        UITheme.styleTitleLabel(titleLabel);
        titleLabel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));

        // Вкладки
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(UITheme.BACKGROUND_LIGHT);

        // Вкладка "Каталог книг"
        JPanel catalogPanel = createCatalogPanel();
        tabbedPane.addTab("Каталог книг", catalogPanel);

        // Вкладка "Моя история"
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("Моя история", historyPanel);

        // Панель с кнопкой выхода
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Выход");
        UITheme.styleButton(logoutButton);
        logoutButton.setBackground(UITheme.ERROR_COLOR);
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        // Панель поиска
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        searchPanel.setOpaque(true);
        searchPanel.add(new JLabel("Поиск:") {{
            UITheme.styleLabel(this);
        }});
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

        // Таблица книг
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        // Панель с кнопкой продления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        buttonPanel.setOpaque(true);
        
        JButton extendButton = new JButton("Продлить срок");
        UITheme.styleButton(extendButton);
        extendButton.setOpaque(true);
        extendButton.addActionListener(e -> extendLoan());
        buttonPanel.add(extendButton);

        JButton refreshButton = new JButton("Обновить");
        UITheme.styleButton(refreshButton);
        refreshButton.setBackground(UITheme.TEXT_SECONDARY);
        refreshButton.setOpaque(true);
        refreshButton.addActionListener(e -> loadHistory());
        buttonPanel.add(refreshButton);

        // Таблица истории
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadBooks() {
        booksTableModel.setRowCount(0);
        List<Book> books = dbManager.getAllBooks();
        for (Book book : books) {
            booksTableModel.addRow(new Object[]{
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
        booksTableModel.setRowCount(0);
        List<Book> books = dbManager.searchBooks(query);
        for (Book book : books) {
            booksTableModel.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getCopiesAvailable(),
                book.getCopiesTotal()
            });
        }
    }

    private void loadHistory() {
        historyTableModel.setRowCount(0);
        if (currentReader != null && currentReader.getId() > 0) {
            List<Loan> loans = dbManager.getLoansByReaderId(currentReader.getId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            
            for (Loan loan : loans) {
                Book book = dbManager.getBookById(loan.getBookId());
                String bookTitle = book != null ? book.getTitle() : "Неизвестно";
                String status = loan.getStatus();
                if (loan.isOverdue()) {
                    status = "ПРОСРОЧЕНО";
                }
                
                historyTableModel.addRow(new Object[]{
                    loan.getId(),
                    bookTitle,
                    loan.getIssueDate().format(formatter),
                    loan.getDueDate().format(formatter),
                    status
                });
            }
        }
    }

    private void extendLoan() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showErrorMessage(this, "Выберите книгу для продления");
            return;
        }

        int loanId = (Integer) historyTableModel.getValueAt(selectedRow, 0);
        Loan loan = dbManager.getLoanById(loanId);

        if (loan == null || !loan.isActive()) {
            UITheme.showErrorMessage(this, "Нельзя продлить уже возвращенную книгу");
            return;
        }

        if (loan.isOverdue()) {
            UITheme.showErrorMessage(this, "Нельзя продлить просроченную книгу. Пожалуйста, верните её в библиотеку.");
            return;
        }

        // Продлеваем на 14 дней
        LocalDate newDueDate = loan.getDueDate().plusDays(14);
        dbManager.extendLoan(loanId, newDueDate);
        UITheme.showSuccessMessage(this, "Срок возврата продлен до " + newDueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        loadHistory();
    }
}

