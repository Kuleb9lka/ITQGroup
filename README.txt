Что бы вы поменяли, чтобы обработка одного запроса уверенно работала с
5000+ id?

Ответ: Для поддержки обработки 5000+ ID запрос разбивается на батчи, обрабатывается асинхронно с ограничением concurrency.

Как бы вы вынесли реестр утверждений в отдельную систему (отдельная БД илиотдельный HTTP-сервис)?

Ответ:
Если проект небольшой и реестр не планируется как отдельный продукт, тогда я бы выбрал отдельную БД.
Если реестр -- важный домен, который будет развиваться и требуется изоляция -- HTTP сервис.


1. Запуск PostgreSQL в Docker контейнере.

    - Запускаем Docker Desktop.
    - Открываем терминал в корне проекта ITQGroup (IntelliJ терминал, cmd в корне проекта или Git Bash).
    - Выполняем docker-compose up --build, контейнер базы данных начнёт скачиваться и запускаться.

2. Запуск основного приложения.

    - env файл со всеми данными проекта уже был добавлен в Git репозиторий. Обычно это плохая практика, но для ускорения
     запуска была добавлена.

            APP_PORT=8088. Если меняете порт, обновите api.documents.url в application.yaml сторонней утилиты.

    - Можно включить или выключить воркеры worker.submission.enabled: true/false worker.approval.enabled: true/false,
    изменить размер пакета batch-size: 5, изменить cron cron-expression: */30 * * * * * (запуск каждые 30 секунд).
    - Выполняем mvn clean install. Запуск через IntelliJ IDEA Run (ItqGroupApplication) или Shift + F10.

3. Запуск сторонней API-утилиты.

    - server.port не должен совпадать с портом основного приложения (по умолчанию 8089). Если меняли APP_PORT, обновите
    api.documents.url: http://localhost:<новый порт>.

    - В файл ..\ITQGroup_side_api\src\main\resources\static\document-batch-creation-file.txt впишите любое положительное
     число.
    - Запуск утилиты через IntelliJ IDEA Run (ItqGroupApplication) или Shift + F10.

4. Swagger UI для API.

    - Основное приложение http://localhost:8088/swagger-ui/index.html
    - Утилита http://localhost:8089/swagger-ui/index.html
    - Если меняли порт, замените 8088/8089 на ваш.

5. Логи

    - Логи пишутся в /logs/app.log.
    - Настройки логов можно изменить в application.yaml в разделе logging.level, logging.pattern, logging.rollingpolicy.

6. Минимальные требования. Docker Desktop, Java 17+, Maven, IntelliJ IDEA опционально.





Примеры запросов и ответов основного приложения:

1. Создание документа

POST /documents/create

Запрос:
{
  "authorId": 1,
  "name": "Document name"
}

Ответ:

{
  "id": 10,
  "uniqueNumber": "550e8400-e29b-41d4-a716-446655440000",
  "authorId": 1,
  "name": "Document name",
  "status": "DRAFT",
  "createDate": "26-02-2026 12:00:00",
  "updateDate": "26-02-2026 12:00:00",
  "historyList": []
}

------------------------------------------------------------------------------------------------------------------------

2. Создание пакета документов

POST /documents/batch-create

Запрос:

{
  "authorId": 1,
  "documentQuantityToCreate": 5
}

Ответ (массив коротких DTO):

[
  {
    "id": 11,
    "uniqueNumber": "550e8400-e29b-41d4-a716-446655440001",
    "authorId": 1,
    "name": "Generated document",
    "status": "DRAFT"
  }
]

------------------------------------------------------------------------------------------------------------------------

3. Получение документа по ID

GET /documents/{id}

Ответ:

{
  "id": 10,
  "uniqueNumber": "550e8400-e29b-41d4-a716-446655440000",
  "authorId": 1,
  "name": "Document name",
  "status": "DRAFT",
  "createDate": "26-02-2026 12:00:00",
  "updateDate": "26-02-2026 12:00:00",
  "historyList": [
    {
      "status": "DRAFT",
      "changeDate": "26-02-2026 12:00:00",
      "comment": "Created"
    }
  ]
}

------------------------------------------------------------------------------------------------------------------------

4. Поиск документов

POST /documents/search

Запрос (фильтр):

{
  "status": "DRAFT",
  "authorId": 1,
  "createDateFrom": "01-01-2026 00:00:00",
  "createDateTo": "26-02-2026 00:00:00"
}

Ответ (массив документов):

[
  {
    "id": 10,
    "uniqueNumber": "550e8400-e29b-41d4-a716-446655440000",
    "authorId": 1,
    "name": "Document name",
    "status": "DRAFT",
    "createDate": "26-02-2026 12:00:00",
    "updateDate": "26-02-2026 12:00:00",
    "historyList": []
  }
]

------------------------------------------------------------------------------------------------------------------------

5. Пагинация по списку ID

POST /documents/filter

Запрос:

{
  "documentIds": [10, 11],
  "pageableSettings": {
    "page": 0,
    "size": 10,
    "sortBy": "id",
    "asc": true
  }
}

Ответ:

{
  "content": [
    {
      "id": 10,
      "uniqueNumber": "550e8400-e29b-41d4-a716-446655440000",
      "authorId": 1,
      "name": "Document name",
      "status": "DRAFT",
      "createDate": "26-02-2026 12:00:00",
      "updateDate": "26-02-2026 12:00:00",
      "historyList": []
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "pageNumber": 0,
  "pageSize": 10
}

------------------------------------------------------------------------------------------------------------------------

6. Отправка документов на SUBMISSION

POST /documents/send-submission/{authorId}

Запрос:

[10, 11]

Ответ:

[
  {
    "documentId": 10,
    "status": "SUBMITTED",
    "responseMessage": "Success"
  }
]

------------------------------------------------------------------------------------------------------------------------

7. Отправка документов на APPROVAL

POST /documents/send-approval/{authorId}

Запрос:

[10, 11]

Ответ:

[
  {
    "documentId": 10,
    "status": "APPROVED",
    "responseMessage": "Success"
  }
]

------------------------------------------------------------------------------------------------------------------------



Примеры запросов и ответов утилиты:


1. Проверка конкурентного изменения статуса документа

POST /documents-api

Запрос:

{
  "threads": 5,
  "attempts": 10,
  "authorId": 1,
  "documentId": 10
}

Ответ:

{
  "threads": 5,
  "attemptsPerThread": 10,
  "expectedAttemptsCount": 50,
  "actualAttemptsCount": 50,
  "successAttempts": 1,
  "failedAttempts": 49,
  "finalDocumentStatus": "APPROVED"
}

------------------------------------------------------------------------------------------------------------------------

2. Массовое создание документов из файла

POST /documents-api/file-create

Метод считывает число из файла:

ITQGroup_side_api/src/main/resources/static/document-batch-creation-file.txt

Число в файле — это количество документов, которые будут созданы.

Ответ:
[
  {
    "id": 15,
    "uniqueNumber": "550e8400-e29b-41d4-a716-446655440010",
    "authorId": 1,
    "name": "Generated document",
    "status": "DRAFT",
    "createDate": "26-02-2026 14:10:00",
    "updateDate": "26-02-2026 14:10:00"
  }
]