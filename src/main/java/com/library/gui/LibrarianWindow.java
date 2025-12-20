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

public class LibrarianWindow extends JFrame {
    private String username;
    private DatabaseManager dbManager;
    
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JTable readersTable;
    private DefaultTableModel readersTableModel;
    private JTable loansTable;
    private DefaultTableModel loansTableModel;
    private JTextField searchField;

    public LibrarianWindow(String username) {
        this.username = username;
        this.dbManager = DatabaseManager.getInstance();
        initializeComponents();
        setupLayout();
        loadBooks();
        loadReaders();
        loadLoans();
    }

    private void initializeComponents() {
        setTitle("Библиотечная система - Библиотекарь: " + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1600, 900);
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

        // Таблица читателей
        String[] readerColumns = {"ID", "ФИО", "Номер билета", "Телефон", "Email", "Статус"};
        readersTableModel = new DefaultTableModel(readerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        readersTable = new JTable(readersTableModel);
        UITheme.styleTable(readersTable);

        // Таблица выдач
        String[] loanColumns = {"ID", "Книга", "Читатель", "Дата выдачи", "Срок возврата", "Статус"};
        loansTableModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        loansTable = new JTable(loansTableModel);
        UITheme.styleTable(loansTable);

        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        // Заголовок
        JLabel titleLabel = new JLabel("Кабинет библиотекаря: " + username);
        UITheme.styleTitleLabel(titleLabel);
        titleLabel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));

        // Вкладки
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(UITheme.BACKGROUND_LIGHT);

        // Вкладка "Каталог книг"
        JPanel catalogPanel = createCatalogPanel();
        tabbedPane.addTab("Каталог книг", catalogPanel);

        // Вкладка "Читатели"
        JPanel readersPanel = createReadersPanel();
        tabbedPane.addTab("Читатели", readersPanel);

        // Вкладка "Выдачи"
        JPanel loansPanel = createLoansPanel();
        tabbedPane.addTab("Выдачи", loansPanel);

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

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        buttonPanel.setOpaque(true);
        
        JButton addBookButton = new JButton("Добавить книгу");
        UITheme.styleButton(addBookButton);
        addBookButton.setOpaque(true);
        addBookButton.addActionListener(e -> showAddBookDialog());
        buttonPanel.add(addBookButton);

        JButton editBookButton = new JButton("Редактировать");
        UITheme.styleButton(editBookButton);
        editBookButton.setOpaque(true);
        editBookButton.addActionListener(e -> showEditBookDialog());
        buttonPanel.add(editBookButton);

        JButton deleteBookButton = new JButton("Удалить");
        UITheme.styleButton(deleteBookButton);
        deleteBookButton.setBackground(UITheme.ERROR_COLOR);
        deleteBookButton.setOpaque(true);
        deleteBookButton.addActionListener(e -> deleteBook());
        buttonPanel.add(deleteBookButton);

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

        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setBackground(UITheme.BACKGROUND_LIGHT);
        topControls.add(buttonPanel, BorderLayout.NORTH);
        topControls.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topControls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReadersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        buttonPanel.setOpaque(true);
        
        JButton addReaderButton = new JButton("Зарегистрировать читателя");
        UITheme.styleButton(addReaderButton);
        addReaderButton.setOpaque(true);
        addReaderButton.addActionListener(e -> showAddReaderDialog());
        buttonPanel.add(addReaderButton);

        JButton editReaderButton = new JButton("Редактировать");
        UITheme.styleButton(editReaderButton);
        editReaderButton.setOpaque(true);
        editReaderButton.addActionListener(e -> showEditReaderDialog());
        buttonPanel.add(editReaderButton);

        JButton refreshButton = new JButton("Обновить");
        UITheme.styleButton(refreshButton);
        refreshButton.setBackground(UITheme.TEXT_SECONDARY);
        refreshButton.setOpaque(true);
        refreshButton.addActionListener(e -> loadReaders());
        buttonPanel.add(refreshButton);

        // Таблица читателей
        JScrollPane scrollPane = new JScrollPane(readersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_LIGHT);
        buttonPanel.setOpaque(true);
        
        JButton issueButton = new JButton("Оформить выдачу");
        UITheme.styleButton(issueButton);
        issueButton.setOpaque(true);
        issueButton.addActionListener(e -> showIssueBookDialog());
        buttonPanel.add(issueButton);

        JButton returnButton = new JButton("Оформить возврат");
        UITheme.styleButton(returnButton);
        returnButton.setOpaque(true);
        returnButton.addActionListener(e -> returnBook());
        buttonPanel.add(returnButton);

        JButton refreshButton = new JButton("Обновить");
        UITheme.styleButton(refreshButton);
        refreshButton.setBackground(UITheme.TEXT_SECONDARY);
        refreshButton.setOpaque(true);
        refreshButton.addActionListener(e -> loadLoans());
        buttonPanel.add(refreshButton);

        // Таблица выдач
        JScrollPane scrollPane = new JScrollPane(loansTable);
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

    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Добавить книгу", true);
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BACKGROUND_LIGHT);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField yearField = new JTextField(20);
        JTextField copiesField = new JTextField(20);

        UITheme.styleTextField(titleField);
        UITheme.styleTextField(authorField);
        UITheme.styleTextField(yearField);
        UITheme.styleTextField(copiesField);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Название:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Автор:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Год:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Количество экземпляров:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(copiesField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton saveButton = new JButton("Сохранить");
        UITheme.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                int copies = Integer.parseInt(copiesField.getText().trim());

                if (title.isEmpty() || author.isEmpty()) {
                    UITheme.showErrorMessage(dialog, "Заполните все поля");
                    return;
                }

                Book book = new Book(title, author, year, copies);
                dbManager.addBook(book);
                UITheme.showSuccessMessage(dialog, "Книга добавлена");
                loadBooks();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                UITheme.showErrorMessage(dialog, "Неверный формат числа");
            }
        });
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditBookDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showErrorMessage(this, "Выберите книгу для редактирования");
            return;
        }

        int bookId = (Integer) booksTableModel.getValueAt(selectedRow, 0);
        Book book = dbManager.getBookById(bookId);
        if (book == null) return;

        JDialog dialog = new JDialog(this, "Редактировать книгу", true);
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BACKGROUND_LIGHT);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(book.getTitle(), 20);
        JTextField authorField = new JTextField(book.getAuthor(), 20);
        JTextField yearField = new JTextField(String.valueOf(book.getYear()), 20);
        JTextField copiesTotalField = new JTextField(String.valueOf(book.getCopiesTotal()), 20);
        JTextField copiesAvailableField = new JTextField(String.valueOf(book.getCopiesAvailable()), 20);

        UITheme.styleTextField(titleField);
        UITheme.styleTextField(authorField);
        UITheme.styleTextField(yearField);
        UITheme.styleTextField(copiesTotalField);
        UITheme.styleTextField(copiesAvailableField);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Название:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Автор:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Год:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Всего экземпляров:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(copiesTotalField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Доступно:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(copiesAvailableField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton saveButton = new JButton("Сохранить");
        UITheme.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            try {
                book.setTitle(titleField.getText().trim());
                book.setAuthor(authorField.getText().trim());
                book.setYear(Integer.parseInt(yearField.getText().trim()));
                book.setCopiesTotal(Integer.parseInt(copiesTotalField.getText().trim()));
                book.setCopiesAvailable(Integer.parseInt(copiesAvailableField.getText().trim()));

                dbManager.updateBook(book);
                UITheme.showSuccessMessage(dialog, "Книга обновлена");
                loadBooks();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                UITheme.showErrorMessage(dialog, "Неверный формат числа");
            }
        });
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showErrorMessage(this, "Выберите книгу для удаления");
            return;
        }

        int result = UITheme.showConfirmDialog(this, "Вы уверены, что хотите удалить эту книгу?");
        if (result == JOptionPane.YES_OPTION) {
            int bookId = (Integer) booksTableModel.getValueAt(selectedRow, 0);
            dbManager.deleteBook(bookId);
            UITheme.showSuccessMessage(this, "Книга удалена");
            loadBooks();
        }
    }

    private void loadReaders() {
        readersTableModel.setRowCount(0);
        List<Reader> readers = dbManager.getAllReaders();
        for (Reader reader : readers) {
            readersTableModel.addRow(new Object[]{
                reader.getId(),
                reader.getFullName(),
                reader.getCardNumber(),
                reader.getPhone(),
                reader.getEmail(),
                reader.getStatus()
            });
        }
    }

    private void showAddReaderDialog() {
        JDialog dialog = new JDialog(this, "Регистрация читателя", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BACKGROUND_LIGHT);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField fullNameField = new JTextField(20);
        JTextField cardNumberField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField addressField = new JTextField(20);

        UITheme.styleTextField(fullNameField);
        UITheme.styleTextField(cardNumberField);
        UITheme.styleTextField(phoneField);
        UITheme.styleTextField(emailField);
        UITheme.styleTextField(addressField);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ФИО:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Номер читательского билета:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(cardNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Телефон:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Адрес:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton saveButton = new JButton("Зарегистрировать");
        UITheme.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String cardNumber = cardNumberField.getText().trim();

            if (fullName.isEmpty() || cardNumber.isEmpty()) {
                UITheme.showErrorMessage(dialog, "Заполните обязательные поля (ФИО, Номер билета)");
                return;
            }

            if (dbManager.getReaderByCardNumber(cardNumber) != null) {
                UITheme.showErrorMessage(dialog, "Читатель с таким номером билета уже существует");
                return;
            }

            Reader reader = new Reader(
                fullName,
                cardNumber,
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressField.getText().trim()
            );
            dbManager.addReader(reader);
            UITheme.showSuccessMessage(dialog, "Читатель зарегистрирован");
            loadReaders();
            dialog.dispose();
        });
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditReaderDialog() {
        int selectedRow = readersTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showErrorMessage(this, "Выберите читателя для редактирования");
            return;
        }

        int readerId = (Integer) readersTableModel.getValueAt(selectedRow, 0);
        Reader reader = dbManager.getReaderById(readerId);
        if (reader == null) return;

        JDialog dialog = new JDialog(this, "Редактировать читателя", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BACKGROUND_LIGHT);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField fullNameField = new JTextField(reader.getFullName(), 20);
        JTextField cardNumberField = new JTextField(reader.getCardNumber(), 20);
        JTextField phoneField = new JTextField(reader.getPhone(), 20);
        JTextField emailField = new JTextField(reader.getEmail(), 20);
        JTextField addressField = new JTextField(reader.getAddress(), 20);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"ACTIVE", "BLOCKED"});
        statusCombo.setSelectedItem(reader.getStatus());

        UITheme.styleTextField(fullNameField);
        UITheme.styleTextField(cardNumberField);
        UITheme.styleTextField(phoneField);
        UITheme.styleTextField(emailField);
        UITheme.styleTextField(addressField);
        UITheme.styleComboBox(statusCombo);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ФИО:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Номер читательского билета:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(cardNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Телефон:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Адрес:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Статус:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton saveButton = new JButton("Сохранить");
        UITheme.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            reader.setFullName(fullNameField.getText().trim());
            reader.setCardNumber(cardNumberField.getText().trim());
            reader.setPhone(phoneField.getText().trim());
            reader.setEmail(emailField.getText().trim());
            reader.setAddress(addressField.getText().trim());
            reader.setStatus((String) statusCombo.getSelectedItem());

            dbManager.updateReader(reader);
            UITheme.showSuccessMessage(dialog, "Читатель обновлен");
            loadReaders();
            dialog.dispose();
        });
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadLoans() {
        loansTableModel.setRowCount(0);
        List<Loan> loans = dbManager.getAllLoans();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        
        for (Loan loan : loans) {
            Book book = dbManager.getBookById(loan.getBookId());
            Reader reader = dbManager.getReaderById(loan.getReaderId());
            
            String bookTitle = book != null ? book.getTitle() : "Неизвестно";
            String readerName = reader != null ? reader.getFullName() : "Неизвестно";
            String status = loan.getStatus();
            if (loan.isOverdue()) {
                status = "ПРОСРОЧЕНО";
            }
            
            loansTableModel.addRow(new Object[]{
                loan.getId(),
                bookTitle,
                readerName,
                loan.getIssueDate().format(formatter),
                loan.getDueDate().format(formatter),
                status
            });
        }
    }

    private void showIssueBookDialog() {
        JDialog dialog = new JDialog(this, "Оформить выдачу книги", true);
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BACKGROUND_LIGHT);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BACKGROUND_LIGHT);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField bookIdField = new JTextField(20);
        JTextField readerCardField = new JTextField(20);
        JTextField daysField = new JTextField("14", 20);

        UITheme.styleTextField(bookIdField);
        UITheme.styleTextField(readerCardField);
        UITheme.styleTextField(daysField);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID книги:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(bookIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Номер читательского билета:") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(readerCardField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Срок выдачи (дней):") {{
            UITheme.styleLabel(this);
        }}, gbc);
        gbc.gridx = 1;
        panel.add(daysField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton issueButton = new JButton("Оформить выдачу");
        UITheme.styleButton(issueButton);
        issueButton.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(bookIdField.getText().trim());
                String cardNumber = readerCardField.getText().trim();
                int days = Integer.parseInt(daysField.getText().trim());

                Book book = dbManager.getBookById(bookId);
                if (book == null) {
                    UITheme.showErrorMessage(dialog, "Книга не найдена");
                    return;
                }

                if (!book.isAvailable()) {
                    UITheme.showErrorMessage(dialog, "Нет доступных экземпляров этой книги");
                    return;
                }

                Reader reader = dbManager.getReaderByCardNumber(cardNumber);
                if (reader == null) {
                    UITheme.showErrorMessage(dialog, "Читатель не найден");
                    return;
                }

                if (!reader.isActive()) {
                    UITheme.showErrorMessage(dialog, "Читатель заблокирован");
                    return;
                }

                Loan loan = new Loan(bookId, reader.getId(), LocalDate.now().plusDays(days));
                dbManager.addLoan(loan);
                UITheme.showSuccessMessage(dialog, "Книга выдана");
                loadLoans();
                loadBooks();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                UITheme.showErrorMessage(dialog, "Неверный формат числа");
            }
        });
        panel.add(issueButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void returnBook() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showErrorMessage(this, "Выберите выдачу для возврата");
            return;
        }

        int loanId = (Integer) loansTableModel.getValueAt(selectedRow, 0);
        Loan loan = dbManager.getLoanById(loanId);

        if (loan == null || !loan.isActive()) {
            UITheme.showErrorMessage(this, "Эта книга уже возвращена");
            return;
        }

        dbManager.returnLoan(loanId);
        UITheme.showSuccessMessage(this, "Книга возвращена");
        loadLoans();
        loadBooks();
    }
}

