**LinkTracker** — это многомодульный backend-проект для отслеживания обновлений по ссылкам и уведомления пользователей в Telegram.

Проект состоит из двух основных сценариев:
- пользователь добавляет ссылку через Telegram-бота;
- сервис мониторинга периодически проверяет источник и отправляет уведомление при изменениях.

## Что умеет проект

- регистрировать чаты и управлять списком отслеживаемых ссылок;
- добавлять и удалять ссылки из отслеживания;
- поддерживать фильтрацию/метки (теги) для ссылок;
- проверять обновления по GitHub и Stack Overflow;
- отправлять уведомления пользователю через Telegram.

## Архитектура

Проект организован как Maven multi-module:

Сечас реализовнны :
- `bot` — Telegram-бот и HTTP API для получения обновлений от `scrapper`;
- `scrapper` — сервис подписок и планировщик проверки ссылок;  

В будущем также планируются :
- `ai-agent` — отдельный сервис для AI-сценариев;
- `build-report-aggregate` — служебный модуль для агрегации отчётов сборки.

## Технологический стек

- **Java 25**
- **Spring Boot** 
- **PostgreSQL**
- **Liquibase** - для миграций схемы БД
- **Spring Data JDBC/JPA**
- **OpenAPI**
- **Telegram Bot API** 
- **Maven Wrapper** - для воспроизводимой сборки
- **Testcontainers, WireMock, JUnit** - для тестирования

## Конфигурация

Токен не хранится в репозитории и должен быть задан локальнo:
локальный конфиг или через Environment variables TELEGRAM_TOKEN=your_token_here.

### Вариант : локальный конфиг

Создать файл `application-local.yaml`:

```yaml
app:
    telegram:
        token: your_token_here
```

В конфигурации используется `application.yaml`

База данных по умолчанию для `scrapper`:

- `jdbc:postgresql://localhost:5432/linktracker`
- `username: postgres`
- `password: postgres
## Быстрый старт

### 1) Поднять PostgreSQL

```bash
docker compose up -d postgres
```

### 2) Собрать проект

```bash
./mvnw clean verify
```

### 3) Запустить сервисы

Запуск конкретного модуля:

```bash
./mvnw -pl bot spring-boot:run
./mvnw -pl scrapper spring-boot:run
./mvnw -pl ai-agent spring-boot:run
```

Или запуск из IDE с главным классом нужного модуля.
## Запуск тестов

```bash
./mvnw test
```

## Команды бота

Бот поддерживает базовые команды управления подписками:

- `/start`
- `/help`
- `/track`
- `/untrack`
- `/list`

Полезную для разработки проекта информацию вы можете найти в файле [HELP.md](./HELP.md).
