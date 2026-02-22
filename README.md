# LinkTracker

LinkTracker – Telegram-бот, который отслеживает изменения на веб-страницах и оперативно информирует пользователя о них.
Проект реализован на Spring Boot и использует Telegram Bot API

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

## Запуск приложения

```bash
./mvnw spring-boot:run
```

или

```bash
mvn spring-boot:run
```

## Запуск тестов

```bash
./mvnw test
```

Полезную для разработки проекта информацию вы можете найти в файле [HELP.md](./HELP.md).
