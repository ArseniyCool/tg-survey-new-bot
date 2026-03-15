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

## Быстрая диагностика

- Если бот не реагирует, проверьте в окне ngrok, что появляются запросы (Connections > 0).
- В веб-интерфейсе ngrok можно посмотреть входящие запросы: `http://127.0.0.1:4040`
- Если порт занят, измените порт в Run Configuration: `MICRONAUT_SERVER_PORT=8090` и запустите `ngrok http 8090`

## Безопасность

- Никогда не коммитьте токен в репозиторий.
- Если токен случайно попал в чат/репозиторий, перевыпустите его через BotFather.