# Учетные записи пользователей системы

Все учетные записи для входа в библиотечную систему.

## Администраторы (ADMIN)

| Логин | Пароль | Роль |
|-------|--------|------|
| admin | admin | ADMIN |
| admin1 | admin123 | ADMIN |
| root | root | ADMIN |

**Всего: 3 администратора**

## Библиотекари (LIBRARIAN)

| Логин | Пароль | Роль |
|-------|--------|------|
| librarian | librarian | LIBRARIAN |
| librarian1 | lib123 | LIBRARIAN |
| librarian2 | lib456 | LIBRARIAN |
| maria | maria123 | LIBRARIAN |
| ivan | ivan123 | LIBRARIAN |

**Всего: 5 библиотекарей**

## Читатели (READER)

| Логин | Пароль | Роль |
|-------|--------|------|
| reader | reader | READER |
| reader1 | read123 | READER |
| reader2 | read456 | READER |
| petrov | petrov123 | READER |
| sidorov | sidorov123 | READER |
| kozlov | kozlov123 | READER |
| volkov | volkov123 | READER |
| novikov | novikov123 | READER |
| morozov | morozov123 | READER |
| sokolov | sokolov123 | READER |

**Всего: 10 читателей**

---

## Итого

**Всего пользователей: 18**

- 3 администратора
- 5 библиотекарей
- 10 читателей

---

## Быстрый доступ

### Для тестирования администратора:
```
Логин: admin
Пароль: admin
```

### Для тестирования библиотекаря:
```
Логин: librarian
Пароль: librarian
```

### Для тестирования читателя:
```
Логин: reader
Пароль: reader
```

---

## Примечание

Эти учетные записи создаются автоматически при первом запуске приложения, если база данных пуста.

**Важно:** Пользователи системы (users) - это учетные записи для входа. Читатели библиотеки (readers) - это отдельные сущности с персональными данными (ФИО, номер билета, контакты). См. [ROLES_AND_USERS.md](ROLES_AND_USERS.md) для подробностей.

