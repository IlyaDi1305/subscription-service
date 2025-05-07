
INSERT INTO users (id, name, email) VALUES (100, 'Тест Пользователь', 'test@example.com');
INSERT INTO users (id, name, email) VALUES (101, 'Анна Иванова', 'anna@example.com');
INSERT INTO users (id, name, email) VALUES (102, 'Павел Петров', 'pavel@example.com');


INSERT INTO subscriptions (id, service_name) VALUES (1, 'Netflix');
INSERT INTO subscriptions (id, service_name) VALUES (2, 'YouTube Premium');
INSERT INTO subscriptions (id, service_name) VALUES (3, 'Яндекс.Плюс');
INSERT INTO subscriptions (id, service_name) VALUES (4, 'VK Музыка');


INSERT INTO user_subscriptions (user_id, subscription_id) VALUES (100, 1);
INSERT INTO user_subscriptions (user_id, subscription_id) VALUES (100, 2);
INSERT INTO user_subscriptions (user_id, subscription_id) VALUES (101, 3);
INSERT INTO user_subscriptions (user_id, subscription_id) VALUES (102, 1);
INSERT INTO user_subscriptions (user_id, subscription_id) VALUES (102, 4);

SELECT setval('subscriptions_id_seq', (SELECT MAX(id) FROM subscriptions));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
