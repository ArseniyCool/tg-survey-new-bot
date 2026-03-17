# Survey Telegram Bot (Webhook)

Бот-опросник: собирает номер телефона, название проекта и назначение. Работает в режиме webhook через ngrok.

## 1) Что нужно установить

- IntelliJ IDEA
- ngrok

## 2) Запуск приложения (локально)

1. Откройте `Main.kt` и создайте Run Configuration (Kotlin). Перейти в ⋮ и в разделе "Configuration" выбрать "Edit…"
2. В открывшемся окне в разделе "Environment variables" добавьте Environment variables:
   - `TELEGRAM_TOKEN=<BOT_TOKEN>`
   - (опционально) `MICRONAUT_SERVER_PORT=8080` (изначально работает по адресу "8080")
3. Запустите `MainKt`.

Приложение поднимает webhook endpoint:
- `POST http://localhost:8080/telegram/webhook`

## 3) Запуск ngrok

Откройте терминал в папке с `ngrok.exe` и выполните:

```powershell
.\ngrok.exe http 8080
```

В выводе ngrok найдите строку вида:
- `Forwarding https://<something>.ngrok-free.dev -> http://localhost:8080`

Скопируйте HTTPS-адрес.

## 4) Подключение webhook в Telegram

В PowerShell выполните (подставьте токен бота и ngrok URL):

```powershell
$token = "<BOT_TOKEN>"
$hook  = "https://<something>.ngrok-free.dev/telegram/webhook"

Invoke-RestMethod "https://api.telegram.org/bot$token/deleteWebhook"
Invoke-RestMethod "https://api.telegram.org/bot$token/setWebhook?url=$hook"
Invoke-RestMethod "https://api.telegram.org/bot$token/getWebhookInfo"
```

Важно:
- webhook работает только по HTTPS.
- URL меняется при каждом новом запуске ngrok, webhook нужно ставить заново.

## 5) Проверка в Telegram

1. Откройте бота в Telegram.
2. Напишите `/start`.
3. Далее отправьте номер телефона, название проекта, назначение.

## 6) Подключение базы данных (PostgreSQL) и проверка сохранения

По умолчанию приложение может работать без БД. Для сохранения анкет в PostgreSQL включается окружение `db`.

1. Установите PostgreSQL и убедитесь, что сервер запущен.
2. Создайте базу данных `telegram_bot`.
   - Пример для PowerShell (пароль задайте свой):

```powershell
$env:PGPASSWORD="123"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -h localhost -p 1111 -U postgres -c "CREATE DATABASE telegram_bot;"
```

3. В IntelliJ IDEA (Run Configuration для `MainKt`) добавьте Environment variables:
   - `MICRONAUT_ENVIRONMENTS=db`
   - `DB_PASSWORD=<ваш_пароль>` (например `123`)
   - (опционально) `DB_USERNAME=postgres`
   - (опционально) `DB_URL=jdbc:postgresql://localhost:1111/telegram_bot`

4. Запустите приложение. Таблица `survey_submissions` создастся автоматически при старте.
5. Проверьте, что анкета записалась:

```powershell
$env:PGPASSWORD="123"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -h localhost -p 1111 -U postgres -d telegram_bot -c "SELECT id, chat_id, phone, project_name, purpose, created_at FROM survey_submissions ORDER BY created_at DESC LIMIT 20;"
```

Примечание про кодировку (если в `psql` отображаются «кракозябры»):

```powershell
chcp 65001
$env:PGCLIENTENCODING="UTF8"
```

## Быстрая диагностика

- Если бот не реагирует, проверьте в окне ngrok, что появляются запросы (Connections > 0).
- В веб-интерфейсе ngrok можно посмотреть входящие запросы: `http://127.0.0.1:4040`
- Если порт занят, измените порт в Run Configuration: `MICRONAUT_SERVER_PORT=8090` и запустите `ngrok http 8090`
- Если включили `MICRONAUT_ENVIRONMENTS=db` и приложение не стартует:
  - проверьте, что PostgreSQL запущен и слушает нужный порт;
  - проверьте переменные `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.

## Безопасность

- Никогда не коммитьте токен в репозиторий.
- Если токен случайно попал в чат/репозиторий, перевыпустите его через BotFather.

