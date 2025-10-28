# Minecraft Message Sender

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.7-green.svg)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Fabric-0.16.14-blue.svg)](https://fabricmc.net)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net)
[![Gradle](https://img.shields.io/badge/Gradle-8.14-green.svg)](https://gradle.org)

Fabric мод для отправки сообщений с использованием Protobuf сериализации и сохранением в PostgreSQL через Hibernate ORM.

## 🚀 Возможности

- **GUI интерфейс** - простой экран с полем ввода и кнопкой отправки
- **Protobuf сериализация** - эффективная передача данных между клиентом и сервером
- **PostgreSQL интеграция** - надежное хранение сообщений с Hibernate ORM
- **Обратная связь** - подтверждения получения сообщений в игровом чате
- **Unit тесты** - полное покрытие функционала (24 теста)
- **Профессиональная документация** - JavaDoc для всех классов

## 📋 Требования

- **Minecraft**: 1.21.7 (стабильная версия)
- **Java**: 21+
- **Fabric Loader**: 0.16.14+
- **PostgreSQL**: 12+ (опционально)

> **⚠️ Примечание о версиях**: В задании указана версия Minecraft 1.21.8, но используется стабильная версия 1.21.7. При появлении стабильной версии 1.21.8 рекомендуется обновить зависимости.

## 🛠️ Установка

### Для разработчиков

1. **Клонируйте репозиторий**:
   ```bash
   git clone https://github.com/AnnaLill/minecraft_message_sender.git
   cd minecraft_message_sender
   ```

2. **Соберите проект**:
   ```bash
   ./gradlew build
   ```

3. **Запустите клиент**:
   ```bash
   ./gradlew runClient
   ```

4. **Запустите сервер** (опционально):
   ```bash
   ./gradlew runServer
   ```

### Для пользователей

1. Скачайте [Fabric Loader 0.16.14+](https://fabricmc.net/use/)
2. Установите мод в папку `mods/`
3. Запустите Minecraft

## 🎮 Использование

1. **Откройте экран сообщений** - нажмите клавишу **M** в игре
2. **Введите сообщение** - в поле ввода (максимум 256 символов)
3. **Отправьте** - нажмите кнопку "Отправить"
4. **Получите подтверждение** - в чате появится "✅ Ваше сообщение получено и сохранено!"

## 🏗️ Архитектура

### Клиентская часть
- `ClientInit` - инициализация клиента, регистрация клавиш
- `MessageSenderScreen` - GUI экран для ввода сообщений
- `ClientPacketHandler` - отправка Protobuf сообщений на сервер

### Серверная часть
- `ExampleMod` - основной мод, обработка сетевых пакетов
- `MessageService` - бизнес-логика работы с сообщениями
- `MessageRepository` - доступ к базе данных через Hibernate
- `MessageEntity` - JPA Entity для таблицы `messages`

### Сетевое взаимодействие
- `MessagePayload` - клиент→сервер (Protobuf данные)
- `ConfirmationPayload` - сервер→клиент (подтверждения)

## 🗄️ База данных

### Схема таблицы
```sql
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    text VARCHAR(256) NOT NULL
);
```

### Настройка PostgreSQL

📖 **[Полная инструкция по настройке БД](DATABASE_SETUP.md)**

Краткая инструкция:
1. Установите PostgreSQL 12+
2. Создайте базу данных:
   ```sql
   CREATE DATABASE minecraft_messages;
   ```
3. Выполните SQL скрипт: `database_init.sql`
4. Настройте подключение в `src/main/resources/hibernate.cfg.xml`

> **💡 Примечание**: Мод работает и без PostgreSQL - сообщения логируются в консоль сервера.

## 🧪 Тестирование

Запуск всех тестов:
```bash
./gradlew test
```

Покрытие тестами:
- **Protobuf** - сериализация/десериализация (5 тестов)
- **Entity** - JPA Entity корректность (8 тестов)
- **Service** - бизнес-логика (5 тестов)
- **Integration** - полный цикл работы (6 тестов)

## 📁 Структура проекта

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── ExampleMod.java              # Основной мод
│   │   ├── client/                       # Клиентская часть
│   │   │   ├── ClientInit.java
│   │   │   ├── gui/MessageSenderScreen.java
│   │   │   └── networking/ClientPacketHandler.java
│   │   └── server/                       # Серверная часть
│   │       └── db/
│   │           ├── entity/MessageEntity.java
│   │           ├── repository/MessageRepository.java
│   │           └── service/
│   │               ├── MessageService.java
│   │               └── MessageServiceImpl.java
│   ├── proto/message.proto               # Protobuf схема
│   └── resources/
│       ├── fabric.mod.json              # Метаданные мода
│       └── hibernate.cfg.xml           # Конфигурация Hibernate
└── test/                                # Unit тесты
    └── java/org/example/
        ├── proto/MessageProtoTest.java
        ├── server/db/entity/MessageEntityTest.java
        ├── server/db/service/MessageServiceTest.java
        └── integration/IntegrationTest.java
```

## 🔧 Технологии

- **Minecraft**: 1.21.7
- **Fabric Loader**: 0.16.14
- **Fabric API**: 0.128.0+1.21.7
- **Java**: 21
- **Gradle**: 8.14
- **Protobuf**: 3.25.3
- **Hibernate**: 6.5.0.Final
- **PostgreSQL**: 42.7.3
- **JUnit**: 5.10.1
- **AssertJ**: 3.25.3

## 📝 Лицензия

MIT License - см. файл [LICENSE](LICENSE)

## 🤝 Вклад в проект

1. Форкните репозиторий
2. Создайте ветку для новой функции (`git checkout -b feature/amazing-feature`)
3. Зафиксируйте изменения (`git commit -m 'Add amazing feature'`)
4. Отправьте в ветку (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📞 Поддержка

Если у вас возникли вопросы или проблемы:
- Создайте [Issue](https://github.com/AnnaLill/minecraft_message_sender/issues)
- Свяжитесь с командой разработки

---

**Разработано командой Minecraft Message Sender** 🎮