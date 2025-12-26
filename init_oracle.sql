-- Скрипт инициализации базы данных Oracle для Web4

-- Создание пользователя (выполнить под пользователем SYSTEM или SYS)
CREATE USER web4user IDENTIFIED BY web4pass;
GRANT CONNECT, RESOURCE TO web4user;
GRANT CREATE SESSION TO web4user;
GRANT UNLIMITED TABLESPACE TO web4user;
GRANT CREATE TABLE TO web4user;
GRANT CREATE SEQUENCE TO web4user;

-- Подключиться как web4user и выполнить следующие команды

-- Создание последовательностей
CREATE SEQUENCE user_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

CREATE SEQUENCE result_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

-- Создание таблицы пользователей
CREATE TABLE users (
    id NUMBER(19) PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password_hash VARCHAR2(255) NOT NULL,
    CONSTRAINT users_username_uk UNIQUE (username)
);

-- Создание таблицы результатов
CREATE TABLE results (
    id NUMBER(19) PRIMARY KEY,
    x NUMBER(10,2) NOT NULL,
    y NUMBER(10,2) NOT NULL,
    r NUMBER(10,2) NOT NULL,
    hit NUMBER(1) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    execution_time VARCHAR2(50),
    user_id NUMBER(19) NOT NULL,
    CONSTRAINT results_user_fk FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

-- Создание индексов для оптимизации запросов
CREATE INDEX idx_results_user_timestamp ON results(user_id, timestamp DESC);
CREATE INDEX idx_users_username ON users(username);

-- Примеры тестовых данных (опционально)
-- Пароль: test123 (SHA-256: ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae)
INSERT INTO users (id, username, password_hash)
VALUES (user_seq.NEXTVAL, 'testuser', 'zpxxFx0RYzFql+OsNAjJg1rYzw88G8cDUnwwJlU091o=');

COMMIT;

-- Проверка созданных объектов
SELECT 'Таблицы:' AS info FROM dual;
SELECT table_name FROM user_tables WHERE table_name IN ('USERS', 'RESULTS');

SELECT 'Последовательности:' AS info FROM dual;
SELECT sequence_name FROM user_sequences WHERE sequence_name IN ('USER_SEQ', 'RESULT_SEQ');

SELECT 'Индексы:' AS info FROM dual;
SELECT index_name, table_name FROM user_indexes
WHERE table_name IN ('USERS', 'RESULTS');

