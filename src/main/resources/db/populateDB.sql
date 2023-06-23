DELETE
FROM meal;
DELETE
FROM user_role;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meal (user_id, description, date_time, calories)
VALUES (100000, 'Завтрак', '2023-06-29 10:00', 500),
       (100000, 'Обед', '2023-06-29 13:00', 1000),
       (100000, 'Ужин', '2023-06-29 20:00', 500),
       (100000, 'Еда на граничное значение', '2023-06-30 00:00', 100),
       (100000, 'Завтрак', '2023-06-30 10:00', 1000),
       (100000, 'Обед', '2023-06-30 13:00', 500),
       (100000, 'Ужин', '2023-06-30 20:00', 410),
       (100001, 'Завтрак Admin', '2023-06-25 09:30', 1000),
       (100001, 'Обед Admin', '2023-06-25 13:30', 1000),
       (100001, 'Ужин Admin', '2023-06-25 20:00', 500)
;
