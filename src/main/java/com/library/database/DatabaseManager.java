package com.library.database;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Reader;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Создание таблицы книг
            String createBooksTable = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "year INTEGER NOT NULL, " +
                    "copies_total INTEGER NOT NULL, " +
                    "copies_available INTEGER NOT NULL)";

            // Создание таблицы читателей
            String createReadersTable = "CREATE TABLE IF NOT EXISTS readers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "full_name TEXT NOT NULL, " +
                    "card_number TEXT UNIQUE NOT NULL, " +
                    "registration_date TEXT NOT NULL, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "address TEXT, " +
                    "status TEXT NOT NULL DEFAULT 'ACTIVE')";

            // Создание таблицы выдач
            String createLoansTable = "CREATE TABLE IF NOT EXISTS loans (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "book_id INTEGER NOT NULL, " +
                    "reader_id INTEGER NOT NULL, " +
                    "issue_date TEXT NOT NULL, " +
                    "return_date TEXT, " +
                    "due_date TEXT NOT NULL, " +
                    "status TEXT NOT NULL DEFAULT 'ACTIVE', " +
                    "FOREIGN KEY (book_id) REFERENCES books(id), " +
                    "FOREIGN KEY (reader_id) REFERENCES readers(id))";

            // Создание таблицы пользователей системы
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL)";

            stmt.execute(createBooksTable);
            stmt.execute(createReadersTable);
            stmt.execute(createLoansTable);
            stmt.execute(createUsersTable);

            // Добавление тестовых данных, если таблицы пусты
            if (isTableEmpty("books")) {
                insertSampleBooks();
            }
            if (isTableEmpty("users")) {
                insertDefaultUsers();
            }
            if (isTableEmpty("readers")) {
                insertSampleReaders();
            }

        } catch (SQLException e) {
            System.err.println("Ошибка инициализации БД: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isTableEmpty(String tableName) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            return true;
        }
    }

    private void insertSampleBooks() {
        List<Book> sampleBooks = List.of(
            // Русская классика
            new Book("Война и мир", "Лев Толстой", 1869, 5),
            new Book("Преступление и наказание", "Федор Достоевский", 1866, 3),
            new Book("Мастер и Маргарита", "Михаил Булгаков", 1967, 4),
            new Book("Анна Каренина", "Лев Толстой", 1877, 4),
            new Book("Идиот", "Федор Достоевский", 1869, 3),
            new Book("Братья Карамазовы", "Федор Достоевский", 1880, 2),
            new Book("Евгений Онегин", "Александр Пушкин", 1833, 6),
            new Book("Отцы и дети", "Иван Тургенев", 1862, 4),
            new Book("Мертвые души", "Николай Гоголь", 1842, 3),
            new Book("Обломов", "Иван Гончаров", 1859, 3),
            
            // Зарубежная классика
            new Book("1984", "Джордж Оруэлл", 1949, 6),
            new Book("Скотный двор", "Джордж Оруэлл", 1945, 5),
            new Book("Великий Гэтсби", "Фрэнсис Скотт Фицджеральд", 1925, 4),
            new Book("Убить пересмешника", "Харпер Ли", 1960, 5),
            new Book("Гордость и предубеждение", "Джейн Остин", 1813, 6),
            new Book("Джейн Эйр", "Шарлотта Бронте", 1847, 4),
            new Book("Три мушкетера", "Александр Дюма", 1844, 5),
            new Book("Граф Монте-Кристо", "Александр Дюма", 1844, 3),
            new Book("Приключения Тома Сойера", "Марк Твен", 1876, 7),
            new Book("Приключения Гекльберри Финна", "Марк Твен", 1884, 5),
            
            // Современная литература
            new Book("Гарри Поттер и философский камень", "Дж. К. Роулинг", 1997, 8),
            new Book("Гарри Поттер и Тайная комната", "Дж. К. Роулинг", 1998, 7),
            new Book("Гарри Поттер и узник Азкабана", "Дж. К. Роулинг", 1999, 6),
            new Book("Властелин колец: Братство кольца", "Дж. Р. Р. Толкин", 1954, 5),
            new Book("Властелин колец: Две крепости", "Дж. Р. Р. Толкин", 1954, 4),
            new Book("Властелин колец: Возвращение короля", "Дж. Р. Р. Толкин", 1955, 4),
            new Book("Хоббит", "Дж. Р. Р. Толкин", 1937, 6),
            new Book("Игра престолов", "Джордж Р. Р. Мартин", 1996, 3),
            new Book("Битва королей", "Джордж Р. Р. Мартин", 1998, 2),
            new Book("Буря мечей", "Джордж Р. Р. Мартин", 2000, 2),
            
            // Детективы и триллеры
            new Book("Убийство в Восточном экспрессе", "Агата Кристи", 1934, 5),
            new Book("Десять негритят", "Агата Кристи", 1939, 4),
            new Book("Шерлок Холмс: Этюд в багровых тонах", "Артур Конан Дойл", 1887, 6),
            new Book("Шерлок Холмс: Знак четырех", "Артур Конан Дойл", 1890, 5),
            new Book("Молчание ягнят", "Томас Харрис", 1988, 3),
            new Book("Код да Винчи", "Дэн Браун", 2003, 7),
            new Book("Ангелы и демоны", "Дэн Браун", 2000, 5),
            
            // Научная фантастика
            new Book("451 градус по Фаренгейту", "Рэй Брэдбери", 1953, 5),
            new Book("Машина времени", "Герберт Уэллс", 1895, 4),
            new Book("Война миров", "Герберт Уэллс", 1898, 4),
            new Book("Дюна", "Фрэнк Герберт", 1965, 3),
            new Book("Основание", "Айзек Азимов", 1951, 4),
            new Book("Я, робот", "Айзек Азимов", 1950, 5),
            new Book("Солярис", "Станислав Лем", 1961, 3),
            
            // Фантастика и фэнтези
            new Book("Американские боги", "Нил Гейман", 2001, 4),
            new Book("Океан в конце дороги", "Нил Гейман", 2013, 5),
            new Book("Имя ветра", "Патрик Ротфус", 2007, 3),
            new Book("Страх мудреца", "Патрик Ротфус", 2011, 2),
            
            // Современная русская литература
            new Book("Метро 2033", "Дмитрий Глуховский", 2005, 6),
            new Book("Метро 2034", "Дмитрий Глуховский", 2009, 5),
            new Book("Чапаев и Пустота", "Виктор Пелевин", 1996, 3),
            new Book("Generation П", "Виктор Пелевин", 1999, 4),
            new Book("День опричника", "Владимир Сорокин", 2006, 2),
            
            // Поэзия
            new Book("Стихотворения", "Александр Пушкин", 1820, 8),
            new Book("Стихотворения", "Сергей Есенин", 1920, 6),
            new Book("Стихотворения", "Владимир Маяковский", 1920, 5),
            new Book("Стихотворения", "Анна Ахматова", 1920, 5)
        );

        for (Book book : sampleBooks) {
            addBook(book);
        }
    }

    private void insertDefaultUsers() {
        // Администраторы
        addSystemUser("admin", "admin", "ADMIN");
        addSystemUser("admin1", "admin123", "ADMIN");
        addSystemUser("root", "root", "ADMIN");
        
        // Библиотекари
        addSystemUser("librarian", "librarian", "LIBRARIAN");
        addSystemUser("librarian1", "lib123", "LIBRARIAN");
        addSystemUser("librarian2", "lib456", "LIBRARIAN");
        addSystemUser("maria", "maria123", "LIBRARIAN");
        addSystemUser("ivan", "ivan123", "LIBRARIAN");
        
        // Читатели
        addSystemUser("reader", "reader", "READER");
        addSystemUser("reader1", "read123", "READER");
        addSystemUser("reader2", "read456", "READER");
        addSystemUser("petrov", "petrov123", "READER");
        addSystemUser("sidorov", "sidorov123", "READER");
        addSystemUser("kozlov", "kozlov123", "READER");
        addSystemUser("volkov", "volkov123", "READER");
        addSystemUser("novikov", "novikov123", "READER");
        addSystemUser("morozov", "morozov123", "READER");
        addSystemUser("sokolov", "sokolov123", "READER");
    }

    private void insertSampleReaders() {
        // Создаем читателей, соответствующих пользователям с ролью READER
        addReader(new Reader("Иван Петров", "R001", "+7-999-111-22-33", "petrov@example.com", "г. Москва, ул. Ленина, д. 1"));
        addReader(new Reader("Петр Сидоров", "R002", "+7-999-222-33-44", "sidorov@example.com", "г. Москва, ул. Пушкина, д. 5"));
        addReader(new Reader("Сергей Козлов", "R003", "+7-999-333-44-55", "kozlov@example.com", "г. Санкт-Петербург, Невский пр., д. 10"));
        addReader(new Reader("Александр Волков", "R004", "+7-999-444-55-66", "volkov@example.com", "г. Москва, ул. Тверская, д. 15"));
        addReader(new Reader("Дмитрий Новиков", "R005", "+7-999-555-66-77", "novikov@example.com", "г. Санкт-Петербург, ул. Невская, д. 20"));
        addReader(new Reader("Андрей Морозов", "R006", "+7-999-666-77-88", "morozov@example.com", "г. Москва, ул. Арбат, д. 25"));
        addReader(new Reader("Михаил Соколов", "R007", "+7-999-777-88-99", "sokolov@example.com", "г. Москва, ул. Садовая, д. 30"));
        addReader(new Reader("Елена Иванова", "R008", "+7-999-888-99-00", "ivanova@example.com", "г. Санкт-Петербург, ул. Литейная, д. 35"));
        addReader(new Reader("Мария Смирнова", "R009", "+7-999-999-00-11", "smirnova@example.com", "г. Москва, ул. Красная, д. 40"));
        addReader(new Reader("Анна Кузнецова", "R010", "+7-999-000-11-22", "kuznetsova@example.com", "г. Москва, ул. Зеленая, д. 45"));
        addReader(new Reader("Ольга Лебедева", "R011", "+7-999-111-22-33", "lebedeva@example.com", "г. Санкт-Петербург, ул. Морская, д. 50"));
        addReader(new Reader("Татьяна Новикова", "R012", "+7-999-222-33-44", "novikova@example.com", "г. Москва, ул. Цветочная, д. 55"));
        addReader(new Reader("Наталья Петрова", "R013", "+7-999-333-44-55", "petrova@example.com", "г. Москва, ул. Солнечная, д. 60"));
        addReader(new Reader("Виктор Орлов", "R014", "+7-999-444-55-66", "orlov@example.com", "г. Санкт-Петербург, ул. Речная, д. 65"));
        addReader(new Reader("Николай Соколов", "R015", "+7-999-555-66-77", "sokolov2@example.com", "г. Москва, ул. Лесная, д. 70"));
        addReader(new Reader("Владимир Павлов", "R016", "+7-999-666-77-88", "pavlov@example.com", "г. Москва, ул. Горная, д. 75"));
        addReader(new Reader("Алексей Семенов", "R017", "+7-999-777-88-99", "semenov@example.com", "г. Санкт-Петербург, ул. Парковая, д. 80"));
        addReader(new Reader("Игорь Голубев", "R018", "+7-999-888-99-00", "golubev@example.com", "г. Москва, ул. Садовая, д. 85"));
        addReader(new Reader("Роман Виноградов", "R019", "+7-999-999-00-11", "vinogradov@example.com", "г. Москва, ул. Вишневая, д. 90"));
        addReader(new Reader("Екатерина Борисова", "R020", "+7-999-000-11-22", "borisova@example.com", "г. Санкт-Петербург, ул. Березовая, д. 95"));
    }

    // ========== BOOKS ==========

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, year, copies_total, copies_available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getYear());
            pstmt.setInt(4, book.getCopiesTotal());
            pstmt.setInt(5, book.getCopiesAvailable());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка добавления книги: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("year"),
                    rs.getInt("copies_total"),
                    rs.getInt("copies_available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения книг: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY title";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("year"),
                    rs.getInt("copies_total"),
                    rs.getInt("copies_available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска книг: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("year"),
                    rs.getInt("copies_total"),
                    rs.getInt("copies_available")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения книги: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, year = ?, copies_total = ?, copies_available = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getYear());
            pstmt.setInt(4, book.getCopiesTotal());
            pstmt.setInt(5, book.getCopiesAvailable());
            pstmt.setInt(6, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка обновления книги: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка удаления книги: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== READERS ==========

    public void addReader(Reader reader) {
        String sql = "INSERT INTO readers (full_name, card_number, registration_date, phone, email, address, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reader.getFullName());
            pstmt.setString(2, reader.getCardNumber());
            pstmt.setString(3, reader.getRegistrationDate().toString());
            pstmt.setString(4, reader.getPhone());
            pstmt.setString(5, reader.getEmail());
            pstmt.setString(6, reader.getAddress());
            pstmt.setString(7, reader.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка добавления читателя: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Reader> getAllReaders() {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT * FROM readers ORDER BY full_name";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                readers.add(new Reader(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("card_number"),
                    LocalDate.parse(rs.getString("registration_date")),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения читателей: " + e.getMessage());
            e.printStackTrace();
        }
        return readers;
    }

    public Reader getReaderById(int id) {
        String sql = "SELECT * FROM readers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Reader(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("card_number"),
                    LocalDate.parse(rs.getString("registration_date")),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения читателя: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Reader getReaderByCardNumber(String cardNumber) {
        String sql = "SELECT * FROM readers WHERE card_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Reader(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("card_number"),
                    LocalDate.parse(rs.getString("registration_date")),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения читателя: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void updateReader(Reader reader) {
        String sql = "UPDATE readers SET full_name = ?, card_number = ?, phone = ?, email = ?, address = ?, status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reader.getFullName());
            pstmt.setString(2, reader.getCardNumber());
            pstmt.setString(3, reader.getPhone());
            pstmt.setString(4, reader.getEmail());
            pstmt.setString(5, reader.getAddress());
            pstmt.setString(6, reader.getStatus());
            pstmt.setInt(7, reader.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка обновления читателя: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== LOANS ==========

    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (book_id, reader_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getReaderId());
            pstmt.setString(3, loan.getIssueDate().toString());
            pstmt.setString(4, loan.getDueDate().toString());
            pstmt.setString(5, loan.getStatus());
            pstmt.executeUpdate();

            // Уменьшаем количество доступных экземпляров
            Book book = getBookById(loan.getBookId());
            if (book != null) {
                book.setCopiesAvailable(book.getCopiesAvailable() - 1);
                updateBook(book);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка добавления выдачи: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Loan> getLoansByReaderId(int readerId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE reader_id = ? ORDER BY issue_date DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, readerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("reader_id"),
                    LocalDate.parse(rs.getString("issue_date")),
                    rs.getString("return_date") != null ? LocalDate.parse(rs.getString("return_date")) : null,
                    LocalDate.parse(rs.getString("due_date")),
                    rs.getString("status")
                );
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения выдач: " + e.getMessage());
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans ORDER BY issue_date DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("reader_id"),
                    LocalDate.parse(rs.getString("issue_date")),
                    rs.getString("return_date") != null ? LocalDate.parse(rs.getString("return_date")) : null,
                    LocalDate.parse(rs.getString("due_date")),
                    rs.getString("status")
                );
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения выдач: " + e.getMessage());
            e.printStackTrace();
        }
        return loans;
    }

    public Loan getLoanById(int id) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Loan(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("reader_id"),
                    LocalDate.parse(rs.getString("issue_date")),
                    rs.getString("return_date") != null ? LocalDate.parse(rs.getString("return_date")) : null,
                    LocalDate.parse(rs.getString("due_date")),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения выдачи: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void returnLoan(int loanId) {
        Loan loan = getLoanById(loanId);
        if (loan != null && loan.isActive()) {
            String sql = "UPDATE loans SET return_date = ?, status = ? WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, LocalDate.now().toString());
                pstmt.setString(2, "RETURNED");
                pstmt.setInt(3, loanId);
                pstmt.executeUpdate();

                // Увеличиваем количество доступных экземпляров
                Book book = getBookById(loan.getBookId());
                if (book != null) {
                    book.setCopiesAvailable(book.getCopiesAvailable() + 1);
                    updateBook(book);
                }
            } catch (SQLException e) {
                System.err.println("Ошибка возврата книги: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void extendLoan(int loanId, LocalDate newDueDate) {
        String sql = "UPDATE loans SET due_date = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newDueDate.toString());
            pstmt.setInt(2, loanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка продления выдачи: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== STATISTICS ==========

    public int getTotalBooks() {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения статистики: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalReaders() {
        String sql = "SELECT COUNT(*) FROM readers";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения статистики: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveLoans() {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'ACTIVE'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения статистики: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getOverdueLoans() {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'ACTIVE' AND due_date < date('now')";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения статистики: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // ========== USERS (Системные пользователи) ==========

    public void addSystemUser(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка добавления пользователя: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Ошибка аутентификации пользователя: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения роли пользователя: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<SystemUser> getAllSystemUsers() {
        List<SystemUser> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new SystemUser(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения пользователей: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public void updateSystemUser(String username, String newPassword, String newRole) {
        String sql = "UPDATE users SET password = ?, role = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, newRole);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка обновления пользователя: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteSystemUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка удаления пользователя: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean systemUserExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка проверки существования пользователя: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static class SystemUser {
        private int id;
        private String username;
        private String password;
        private String role;

        public SystemUser(int id, String username, String password, String role) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }
    }
}

