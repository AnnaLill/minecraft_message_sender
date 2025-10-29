# База данных - Настройка и использование

## 📋 Оглавление

1. [Установка PostgreSQL](#установка-postgresql)
2. [Создание базы данных](#создание-базы-данных)
3. [Настройка Hibernate](#настройка-hibernate)
4. [Проверка подключения](#проверка-подключения)
5. [Архитектура подключения](#архитектура-подключения)

---

## 🔧 Установка PostgreSQL

### Windows

1. Скачайте установщик: https://www.postgresql.org/download/windows/
2. Запустите установщик и следуйте инструкциям
3. Запомните пароль для пользователя `postgres` (по умолчанию)
4. PostgreSQL будет доступен на порту `5432`

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### macOS

```bash
brew install postgresql
brew services start postgresql
```

---

## 🗄️ Создание базы данных

### Шаг 1: Откройте PostgreSQL командную строку

**Windows:**
```cmd
psql -U postgres
```

**Linux/macOS:**
```bash
sudo -u postgres psql
```

### Шаг 2: Выполните SQL скрипт

```sql
-- Создать базу данных
CREATE DATABASE minecraft_messages;

-- Подключиться к базе
\c minecraft_messages

-- Создать таблицу
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    text VARCHAR(256) NOT NULL
);

-- Выйти
\q
```

**Или используйте готовый скрипт:**
```bash
psql -U postgres -f database_init.sql
```

---

## ⚙️ Настройка Hibernate

### Конфигурация в `src/main/resources/hibernate.cfg.xml`

```xml
<property name="hibernate.connection.driver_class">org.postgresql.Driver</property>
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/minecraft_messages</property>
<property name="hibernate.connection.username">postgres</property>
<property name="hibernate.connection.password">ВАШ_ПАРОЛЬ</property>
```

**Важно:** Измените пароль на ваш пароль PostgreSQL!

### Настройки Hibernate

- `hibernate.hbm2ddl.auto=update` - автоматически создаёт/обновляет таблицы
- `hibernate.show_sql=true` - показывает все SQL запросы в консоли
- `hibernate.format_sql=true` - форматирует SQL запросы для читаемости

---

## ✅ Проверка подключения

### 1. Запустите сервер

```bash
./gradlew runServer
```

### 2. Проверьте логи

Если подключение успешно, вы увидите:
```
✅ Hibernate подключен к PostgreSQL!
```

Если нет:
```
⚠️ PostgreSQL не доступна, сообщения будут логироваться в консоль
```

### 3. Отправьте сообщение через GUI

- Нажмите клавишу `M` в игре
- Введите текст
- Нажмите "Отправить"

### 4. Проверьте базу данных

```sql
-- Подключитесь к базе
psql -U postgres -d minecraft_messages

-- Просмотрите сообщения
SELECT * FROM messages;

-- Выйти
\q
```

---

## 🏗️ Архитектура подключения

### Как работает Hibernate ORM

```
┌─────────────────┐
│  Client (GUI)   │
│  Клавиша M      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ ClientInit      │
│ KeyBinding      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ MessageSender   │
│ Screen (GUI)    │
└────────┬────────┘
         │ Protobuf
         ▼
┌─────────────────┐
│ ClientPacket    │
│ Handler         │
└────────┬────────┘
         │ Network
         ▼
┌─────────────────┐
│ ExampleMod      │
│ Server Handler  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ MessageService  │
│ (Interface)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ MessageServiceImpl │
│ (Implementation)│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ MessageRepository│
│ (Hibernate)     │
└────────┬────────┘
         │ Session
         ▼
┌─────────────────┐
│ MessageEntity   │
│ (JPA Entity)    │
└────────┬────────┘
         │ ORM
         ▼
┌─────────────────┐
│ PostgreSQL      │
│ Database        │
└─────────────────┘
```

### Компоненты

#### 1. MessageEntity (JPA Entity)
```java
@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private UUID uuid;
    
    @Column(nullable = false, length = 256)
    private String text;
}
```

**Что делает:**
- `@Entity` - указывает Hibernate, что это сущность БД
- `@Table(name = "messages")` - название таблицы
- `@Id` - первичный ключ
- `@GeneratedValue` - автоинкремент
- `@Column` - настройки колонки

#### 2. MessageRepository (Repository Pattern)
```java
public class MessageRepository {
    private static SessionFactory sessionFactory;
    
    public static void saveMessage(UUID uuid, String text) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            MessageEntity entity = new MessageEntity(uuid, text);
            session.persist(entity);
            session.getTransaction().commit();
        }
    }
}
```

**Что делает:**
- `SessionFactory` - фабрика сессий Hibernate
- `Session` - соединение с БД
- `beginTransaction()` - начало транзакции
- `persist()` - сохранить объект
- `commit()` - зафиксировать изменения

#### 3. Ленивая инициализация

```java
private static SessionFactory getSessionFactory() {
    if (sessionFactory == null && !initializationAttempted) {
        try {
            sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
        } catch (Exception e) {
            // БД недоступна - работаем без неё
        }
    }
    return sessionFactory;
}
```

**Преимущества:**
- Мод работает без БД
- Сообщения логируются в консоль
- Нет crash при недоступности PostgreSQL

---

## 🎯 Почему Hibernate с JPA?

### Преимущества

1. **ООП подход** - работаем с объектами, а не SQL
2. **Автоматизация** - таблицы создаются автоматически
3. **Типобезопасность** - компилятор ловит ошибки
4. **Переносимость** - код не зависит от конкретной БД
5. **Кэширование** - оптимизация запросов

### Сравнение

**Без ORM (сырой JDBC):**
```java
Connection conn = DriverManager.getConnection(url, user, pass);
PreparedStatement stmt = conn.prepareStatement(
    "INSERT INTO messages (uuid, text) VALUES (?, ?)"
);
stmt.setObject(1, uuid);
stmt.setString(2, text);
stmt.executeUpdate();
```

**С Hibernate:**
```java
MessageEntity entity = new MessageEntity(uuid, text);
session.persist(entity);
```

**Разница:**
- Код проще и читаемее
- Меньше ошибок с SQL
- Автоматическое управление транзакциями
- Кэширование и оптимизация

---

## 🔍 Проверка работы

### Тест 1: Проверка Entity

```bash
./gradlew test --tests MessageEntityTest
```

### Тест 2: Проверка Repository

```bash
./gradlew test --tests MessageServiceTest
```

### Тест 3: Полное тестирование

```bash
./gradlew test
```

---

## 📚 Дополнительные ресурсы

- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [JPA Specification](https://jpa.java.net/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Fabric Networking](https://docs.fabricmc.net/)



