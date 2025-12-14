# Web Programming Lab #4 - Area Check Application

**Студент:** Мантуш Даниил Валерьевич  
**Группа:** P3219  
**Вариант:** 4758

---

## Описание проекта

Веб-приложение для проверки попадания точки в заданную область на координатной плоскости. Реализовано с использованием современного стека технологий и множества механизмов защиты.

### Проверяемая область (Вариант 4758):
- **Прямоугольник** - 2 квадрант: `x ∈ [-R, 0]`, `y ∈ [0, R/2]`
- **Треугольник** - 3 квадрант: `x ∈ [-R, 0]`, `y ∈ [-R/2, 0]`, граница: `y = -x/2 - R/2`
- **Четверть окружности** - 1 квадрант: радиус `R/2`, `x² + y² ≤ (R/2)²`

---

## Технологический стек

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **JAX-RS (Jersey)** - RESTful API
- **Spring Data JPA** - работа с базой данных
- **Spring Security** - безопасность
- **Oracle Database** - хранение данных
- **JWT (io.jsonwebtoken)** - аутентификация
- **BCrypt** - хеширование паролей
- **Jakarta Validation** - валидация данных
- **OAuth 2.0** - Google, Yandex

### Frontend
- **Angular 17** (Standalone Components)
- **TypeScript**
- **RxJS** - реактивное программирование
- **Google reCAPTCHA v2**
- **Canvas API** - визуализация графика

### Database
- **Oracle Database 21c XE**
- **Docker Compose** - контейнеризация БД

---

## Архитектура проекта

### Backend Structure

```
src/main/java/com/nlshakal/web4/
├── Web4Application.java                   # Точка входа Spring Boot
│
├── config/                                 # Конфигурация
│   ├── JerseyConfig.java                  # Конфигурация JAX-RS
│   ├── SecurityConfig.java                # Spring Security + CORS
│   ├── WebConfig.java                     # CORS для Spring MVC
│   └── JwtProperties.java                 # Настройки JWT
│
├── resource/                               # JAX-RS Resources (Controllers)
│   ├── AuthResource.java                  # /api/auth/* - аутентификация
│   └── ResultResource.java                # /api/results/* - проверка точек
│
├── service/                                # Бизнес-логика
│   ├── AuthService.java                   # Регистрация, вход
│   ├── ResultService.java                 # Проверка точек, история
│   ├── PasswordHashingService.java        # BCrypt хеширование
│   ├── PasswordValidationService.java     # Валидация сложности паролей
│   ├── LoginAttemptService.java           # Отслеживание попыток входа
│   ├── CaptchaService.java                # Проверка reCAPTCHA
│   ├── RateLimitService.java              # Ограничение частоты запросов
│   ├── AuditService.java                  # Логирование событий безопасности
│   └── oauth/                             # OAuth 2.0 провайдеры
│       ├── OAuthProvider.java             # Интерфейс провайдера
│       ├── OAuthService.java              # Фабрика провайдеров
│       ├── GoogleOAuthProvider.java       # Google OAuth
│       └── YandexOAuthProvider.java       # Yandex OAuth
│
├── repository/                             # Data Access Layer
│   ├── UserRepository.java                # CRUD для User
│   └── ResultRepository.java              # CRUD для Result
│
├── entity/                                 # JPA Entities
│   ├── User.java                          # Пользователь
│   └── Result.java                        # Результат проверки
│
├── dto/                                    # Data Transfer Objects
│   ├── LoginRequest.java                  # Запрос входа/регистрации
│   ├── AuthResponse.java                  # Ответ аутентификации
│   ├── SocialLoginRequest.java            # Запрос OAuth входа
│   ├── PointRequest.java                  # Координаты точки
│   └── ResultResponse.java                # Результат проверки
│
├── util/                                   # Утилиты
│   ├── JwtUtil.java                       # Генерация/валидация JWT
│   ├── AreaCheckUtil.java                 # Проверка попадания в область
│   ├── TokenExtractor.java                # Извлечение JWT из заголовков
│   ├── HttpRequestHelper.java             # Извлечение IP, User-Agent
│   └── ResponseBuilder.java               # Построение HTTP ответов
│
└── exception/                              # Обработка исключений
    └── ValidationExceptionMapper.java     # Обработка ошибок валидации
```

### Frontend Structure

```
frontend/src/app/
├── components/                             # UI компоненты
│   ├── login/                             # Страница входа/регистрации
│   │   ├── login.component.ts            # Логика компонента
│   │   ├── login.component.html          # HTML шаблон
│   │   └── login.component.css           # Стили
│   ├── main/                              # Основная страница
│   │   ├── main.component.ts             # Логика + Canvas рисование
│   │   ├── main.component.html           # HTML шаблон
│   │   └── main.component.css            # Стили
│   └── forbidden/                         # Страница 403
│       ├── forbidden.component.ts
│       ├── forbidden.component.html
│       └── forbidden.component.css
│
├── services/                               # Сервисы Angular
│   ├── auth.service.ts                    # HTTP запросы к /api/auth
│   ├── result.service.ts                  # HTTP запросы к /api/results
│   └── theme.service.ts                   # Переключение темной/светлой темы
│
├── guards/                                 # Route Guards
│   └── auth.guard.ts                      # Проверка аутентификации
│
├── interceptors/                           # HTTP Interceptors
│   └── auth.interceptor.ts                # Добавление JWT в заголовки
│
├── models/                                 # TypeScript интерфейсы
│   ├── auth.model.ts                      # LoginRequest, AuthResponse
│   └── result.model.ts                    # PointRequest, Result
│
├── utils/                                  # Утилиты
│   ├── monster-animation.manager.ts       # Анимация монстра на логине
│   ├── captcha.manager.ts                 # Управление reCAPTCHA
│   ├── password-validator.ts              # Визуальная индикация силы пароля
│   └── cookie.service.ts                  # Работа с HTTP Cookies
│
├── app.routes.ts                           # Маршрутизация
├── app.config.ts                           # Конфигурация приложения
└── app.component.ts                        # Корневой компонент
```

---

## API Endpoints

### Authentication (`/api/auth`)

#### `POST /api/auth/register`
Регистрация нового пользователя

**Request Body:**
```json
{
  "username": "user@example.com",
  "password": "SecurePass123",
  "captchaToken": "03AGdBq27..." 
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user@example.com",
  "message": "Регистрация успешна",
  "requiresCaptcha": false,
  "failedAttempts": 0
}
```

**Validations:**
- Username: email формат
- Password: минимум 8 символов, буквы + цифры
- Уникальность username
- reCAPTCHA (если требуется)

---

#### `POST /api/auth/login`
Вход в систему

**Request Body:**
```json
{
  "username": "user@example.com",
  "password": "SecurePass123",
  "captchaToken": "03AGdBq27..."
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user@example.com",
  "message": "Вход выполнен успешно",
  "requiresCaptcha": false,
  "failedAttempts": 0
}
```

**Response (401 Unauthorized) - неверный пароль:**
```json
{
  "message": "Неверные учетные данные",
  "requiresCaptcha": true,
  "failedAttempts": 5
}
```

**Security:**
- Rate Limiting (429 Too Many Requests)
- Captcha после 5 неудачных попыток
- Аудит подозрительной активности после 10 попыток

---

#### `POST /api/auth/social`
OAuth 2.0 вход (Google/Yandex)

**Request Body:**
```json
{
  "code": "4/0AQlEd8yN...",
  "provider": "google"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user@gmail.com",
  "message": "Вход через google выполнен",
  "requiresCaptcha": false,
  "failedAttempts": 0
}
```

**Supported providers:** `google`, `yandex`

---

### Results (`/api/results`)

Все endpoints требуют JWT токен в заголовке:
```
Authorization: Bearer <JWT_TOKEN>
```

#### `POST /api/results/check`
Проверка попадания точки в область

**Request Body:**
```json
{
  "x": 1.5,
  "y": 2.0,
  "r": 2.5
}
```

**Validations:**
- `x` ∈ [-5, 3]
- `y` ∈ [-3, 5]
- `r` ∈ (0, 3]

**Response (200 OK):**
```json
{
  "id": 123,
  "x": 1.5,
  "y": 2.0,
  "r": 2.5,
  "hit": true,
  "timestamp": "2025-12-11T14:30:00",
  "executionTime": 1250
}
```

**Response (400 Bad Request) - валидация:**
```json
{
  "error": "Validation failed",
  "details": [
    "X должен быть >= -5",
    "Y должен быть <= 5"
  ]
}
```

---

#### `GET /api/results`
Получение всех результатов пользователя

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 123,
    "x": 1.5,
    "y": 2.0,
    "r": 2.5,
    "hit": true,
    "timestamp": "2025-12-11T14:30:00",
    "executionTime": 1250
  }
]
```

---

#### `DELETE /api/results`
Очистка всех результатов пользователя

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "message": "Results cleared successfully"
}
```

---

## Механизмы защиты

### 1. **Аутентификация и Авторизация**

#### JWT (JSON Web Tokens)
- **Алгоритм:** HS256 (HMAC-SHA256)
- **Срок действия:** 24 часа (86400000 мс)
- **Секретный ключ:** 256-бит, хранится в переменных окружения
- **Хранение на клиенте:** HTTP Cookies (SameSite=Strict)
- **Передача:** HTTP заголовок `Authorization: Bearer <token>`
- **Payload:** `username` (subject) + `userId` (claim)

**Класс:** `JwtUtil.java`
```java
public String generateToken(String username, Long userId);
public boolean validateToken(String token);
public String getUsernameFromToken(String token);
public Long getUserIdFromToken(String token);
```

**Оптимизация:** `userId` хранится в JWT payload, что **исключает запросы в БД** при каждом HTTP запросе. Это соответствует принципу работы JWT - stateless аутентификация без обращения к базе данных.

---

### 2. **Хеширование паролей**

#### BCrypt
- **Алгоритм:** BCrypt (адаптивное хеширование)
- **Cost Factor:** По умолчанию Spring (10 раундов)
- **Соль:** Автоматическая генерация для каждого пароля
- **Защита:** Rainbow tables, brute-force

**Класс:** `PasswordHashingService.java`
```java
public String hash(String rawPassword);
public boolean verify(String rawPassword, String hashedPassword);
```

**Пример хеша:**
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

---

### 3. **Валидация паролей**

#### Требования к паролю
- Минимум 8 символов
- Минимум 1 буква
- Минимум 1 цифра
- Рекомендуется: буквы верхнего/нижнего регистра, спецсимволы

**Класс:** `PasswordValidationService.java`
```java
private static final Pattern PASSWORD_PATTERN = Pattern.compile(
    "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$"
);
```

**Дополнительные проверки:**
- Проверка на распространенные пароли
- Валидация уникальности

---

### 4. **Rate Limiting (Ограничение частоты запросов)**

#### Механизм
- **Лимит:** 10 запросов с одного IP в минуту
- **Метод:** In-memory счетчик с таймстампами
- **Ответ при превышении:** `429 Too Many Requests`

**Класс:** `RateLimitService.java`
```java
public boolean isAllowed(String ipAddress);
public void resetIP(String ipAddress);
```

**Применяется к:**
- `/api/auth/register`
- `/api/auth/login`
- `/api/auth/social`

---

### 5. **Защита от брутфорса**

#### Механизм отслеживания попыток входа
- **Хранение:** In-memory HashMap
- **Счетчик:** Количество неудачных попыток на username
- **Капча:** Обязательна после 5 неудачных попыток
- **Аудит:** Логирование после 10 неудачных попыток

**Класс:** `LoginAttemptService.java`
```java
public void recordFailedAttempt(String username);
public boolean requiresCaptcha(String username);
public void resetAttempts(String username);
```

---

### 6. **Google reCAPTCHA v2**

#### Интеграция
- **Версия:** reCAPTCHA v2 Checkbox
- **Site Key:** Публичный ключ (frontend)
- **Secret Key:** Серверный ключ (backend)
- **Проверка:** HTTP POST к Google API

**Класс:** `CaptchaService.java`
```java
public boolean verifyCaptcha(String token);
```

**API запрос:**
```
POST https://www.google.com/recaptcha/api/siteverify
Body: secret=...&response=...
```

---

### 7. **OAuth 2.0 (Social Login)**

#### Поддерживаемые провайдеры

**Google OAuth 2.0**
- **Authorization Endpoint:** `https://accounts.google.com/o/oauth2/v2/auth`
- **Token Endpoint:** `https://oauth2.googleapis.com/token`
- **UserInfo Endpoint:** `https://www.googleapis.com/oauth2/v2/userinfo`
- **Scope:** `openid email profile`

**Yandex OAuth 2.0**
- **Authorization Endpoint:** `https://oauth.yandex.ru/authorize`
- **Token Endpoint:** `https://oauth.yandex.ru/token`
- **UserInfo Endpoint:** `https://login.yandex.ru/info`

**Классы:**
- `OAuthProvider.java` - интерфейс (Strategy Pattern)
- `GoogleOAuthProvider.java`
- `YandexOAuthProvider.java`
- `OAuthService.java` - фабрика провайдеров

**Flow:**
1. Frontend редирект на OAuth провайдера
2. Пользователь авторизуется
3. Редирект обратно с `code`
4. Backend обменивает `code` на `access_token`
5. Получение email пользователя
6. Автоматическая регистрация (если нет в БД)
7. Генерация JWT токена

---

### 8. **Валидация входных данных**

#### Jakarta Validation (JSR-380)

**PointRequest.java:**
```java
@NotNull(message = "X не может быть null")
@DecimalMin(value = "-5.0", message = "X должен быть >= -5")
@DecimalMax(value = "3.0", message = "X должен быть <= 3")
private Double x;

@NotNull(message = "Y не может быть null")
@DecimalMin(value = "-3.0", message = "Y должен быть >= -3")
@DecimalMax(value = "5.0", message = "Y должен быть <= 5")
private Double y;

@NotNull(message = "R не может быть null")
@DecimalMin(value = "0.0", inclusive = false, message = "R должен быть > 0")
@DecimalMax(value = "3.0", message = "R должен быть <= 3")
private Double r;
```

**Обработчик:** `ValidationExceptionMapper.java`

---

### 9. **CORS (Cross-Origin Resource Sharing)**

#### Настройки
- **Allowed Origins:** `http://localhost:4200`, `http://localhost:8080`
- **Allowed Methods:** `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- **Allowed Headers:** `*`
- **Allow Credentials:** `true`
- **Max Age:** 3600 секунд

**Класс:** `SecurityConfig.java`

---

### 10. **Spring Security**

#### Конфигурация
- **CSRF:** Отключен (REST API)
- **Session Management:** STATELESS (JWT)
- **Frame Options:** DENY (защита от clickjacking)
- **Content Security Policy (CSP):**
  ```
  default-src 'self';
  script-src 'self' 'unsafe-inline' 'unsafe-eval' https://www.google.com https://www.gstatic.com;
  style-src 'self' 'unsafe-inline' https://fonts.googleapis.com;
  font-src 'self' https://fonts.gstatic.com;
  img-src 'self' data: https:;
  frame-src https://www.google.com;
  connect-src 'self' https://www.google.com;
  ```

**Класс:** `SecurityConfig.java`

---

### 11. **Аудит безопасности**

#### Логируемые события
- Успешная регистрация
- Успешный вход
- Неудачный вход (причина)
- Подозрительная активность (множественные неудачные попытки)

**Класс:** `AuditService.java`
```java
public void logRegistration(String username, String ipAddress, String authProvider);
public void logSuccessfulLogin(String username, String ipAddress, String userAgent);
public void logFailedLogin(String username, String ipAddress, String reason, String userAgent);
public void logSuspiciousActivity(String username, String ipAddress, String details);
```

**Формат лога:**
```
[AUDIT] 2025-12-11 14:30:00 | SUCCESSFUL_LOGIN | user@example.com | 192.168.1.1 | Chrome/120
[AUDIT] 2025-12-11 14:35:00 | FAILED_LOGIN | hacker@evil.com | 10.0.0.1 | Неверный пароль
[AUDIT] 2025-12-11 14:40:00 | SUSPICIOUS | hacker@evil.com | 10.0.0.1 | 10+ неудачных попыток
```

---

### 12. **Защита на Frontend**

#### Auth Guard
- **Проверка:** Наличие JWT токена в cookies
- **Действие:** Редирект на `/login` если не авторизован

**Класс:** `auth.guard.ts`

#### Auth Interceptor
- **Действие:** Автоматическое добавление JWT в заголовок `Authorization`
- **Применение:** Ко всем HTTP запросам к API

**Класс:** `auth.interceptor.ts`

---

## База данных

### Схема (Oracle Database)

#### Таблица `USERS`
```sql
CREATE TABLE users (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR2(255) NOT NULL UNIQUE,
    password_hash VARCHAR2(255),
    auth_provider VARCHAR2(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Поля:**
- `id` - Первичный ключ (автоинкремент)
- `username` - Email пользователя (уникальный)
- `password_hash` - BCrypt хеш пароля (NULL для OAuth пользователей)
- `auth_provider` - Провайдер: `local`, `google`, `yandex`
- `created_at` - Дата регистрации

---

#### Таблица `RESULTS`
```sql
CREATE TABLE results (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    x NUMBER(10,6) NOT NULL,
    y NUMBER(10,6) NOT NULL,
    r NUMBER(10,6) NOT NULL,
    hit NUMBER(1) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    execution_time NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**Поля:**
- `id` - Первичный ключ (автоинкремент)
- `x`, `y`, `r` - Координаты и радиус
- `hit` - Попадание (1 - да, 0 - нет)
- `timestamp` - Время проверки
- `execution_time` - Время выполнения (микросекунды)
- `user_id` - Внешний ключ на `users(id)`

**Индексы:**
- Первичный ключ на `id`
- Индекс на `user_id` (для быстрого поиска по пользователю)

---

## Запуск проекта

### Требования
- **Java:** 17+
- **Node.js:** 18+
- **Docker:** 20+ (для Oracle DB)
- **Gradle:** 8.5+ (встроенный wrapper)
- **Angular CLI:** 17+

---

### 1. Настройка базы данных

**Запуск Oracle DB через Docker:**
```bash
docker-compose up -d
```

**Инициализация схемы:**
```bash
docker exec -it web4-oracle sqlplus web4user/web4pass@xepdb1
@init_oracle.sql
```

**Проверка данных:**
```bash
powershell -File check-db.ps1
```

---

### 2. Настройка переменных окружения

Создайте файл `.env` в корне проекта:
```env
DB_URL=jdbc:oracle:thin:@localhost:1521/xepdb1
DB_USERNAME=web4user
DB_PASSWORD=web4pass

JWT_SECRET=MyVerySecureJwtSecretKeyForWeb4ApplicationThatIsAtLeast256BitsLongToMeetTheRequirementsOfHMACSHA256Algorithm
JWT_EXPIRATION=86400000

CAPTCHA_SECRET=YOUR_RECAPTCHA_SECRET_KEY
CAPTCHA_SITE_KEY=YOUR_RECAPTCHA_SITE_KEY

GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET=YOUR_GOOGLE_CLIENT_SECRET

YANDEX_CLIENT_ID=YOUR_YANDEX_CLIENT_ID
YANDEX_CLIENT_SECRET=YOUR_YANDEX_CLIENT_SECRET

REDIRECT_URI=http://localhost:4200/login
```

---

### 3. Запуск Backend

**Сборка:**
```bash
gradlew clean build
```

**Запуск:**
```bash
gradlew bootRun
```

**Сервер запустится на:** `http://localhost:8080`

**API доступен по:** `http://localhost:8080/web4/api/`

---

### 4. Запуск Frontend

**Установка зависимостей:**
```bash
cd frontend
npm install
```

**Запуск dev сервера:**
```bash
npm start
```

**Приложение доступно на:** `http://localhost:4200`

---

### 5. Сборка для production

**Backend (WAR файл):**
```bash
gradlew bootWar
```
Файл: `build/libs/web4.war`

**Frontend:**
```bash
cd frontend
ng build --configuration production
```
Файлы: `frontend/dist/`

---

## Принципы ООП и паттерны

### Применённые принципы SOLID

#### Single Responsibility Principle (SRP)
Каждый класс отвечает за одну задачу:
- `PasswordHashingService` - только хеширование
- `JwtUtil` - только JWT
- `AreaCheckUtil` - только проверка области

#### Open/Closed Principle (OCP)
Легко расширяемо без изменения существующего кода:
- `OAuthProvider` интерфейс - добавление нового провайдера без изменения `OAuthService`

#### Liskov Substitution Principle (LSP)
Все провайдеры OAuth заменяемы через интерфейс `OAuthProvider`

#### Interface Segregation Principle (ISP)
Интерфейсы узкоспециализированные: `OAuthProvider` содержит только 2 метода

#### Dependency Inversion Principle (DIP)
Зависимости через абстракции + Constructor Injection вместо Field Injection

### Применённые паттерны

#### Strategy Pattern
`OAuthProvider` - интерфейс стратегии, `GoogleOAuthProvider`, `YandexOAuthProvider` - конкретные стратегии

#### Factory Pattern
`OAuthService` - фабрика для получения нужного OAuth провайдера

#### Builder Pattern
`AuthResponse.builder()` - построение сложных объектов ответов

#### Repository Pattern
`UserRepository`, `ResultRepository` - абстракция доступа к данным

#### DTO Pattern
Разделение Entity (JPA) и DTO (API) слоев

---

## Troubleshooting OAuth

### Ошибка 403 при входе через Google/Yandex

**Причины:**
1. **Redirect URI не совпадает** - проверьте, что в консолях Google/Yandex указан `http://localhost:4200/login`
2. **Неверные credentials** - обновите Client ID и Client Secret в `.env`
3. **Заблокированное приложение** - создайте новое OAuth приложение
4. **Истёк authorization code** - попробуйте войти заново

**Диагностика:**

1. Проверьте конфигурацию:
```bash
# Откройте в браузере:
http://localhost:8080/web4/api/health/oauth
```

2. Проверьте логи бэкенда при попытке входа:
```
[Google OAuth] Starting authentication...
[Google OAuth] Error: 401 Unauthorized  # ← Проблема!
```

3. Проверьте консоль браузера (F12):
```
[OAuth] Error: {status: 403, ...}
```

**Решение:**

1. Убедитесь, что в `.env` правильные credentials
2. Перезапустите бэкенд: `gradlew bootRun`
3. Очистите cookies браузера
4. Проверьте Redirect URI в консолях разработчика:
   - Google: https://console.cloud.google.com/apis/credentials
   - Yandex: https://oauth.yandex.ru/

5. Если не помогло - пересоздайте OAuth приложения

---

## Лицензия

Учебный проект для университета ИТМО. Все права защищены.

---

## Автор

**Мантуш Даниил Валерьевич**  
Группа P3219  
Университет ИТМО  
Факультет Программной Инженерии и Компьютерной Техники


