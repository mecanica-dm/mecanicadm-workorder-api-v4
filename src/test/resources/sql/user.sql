DELETE FROM password_reset_tokens;
DELETE FROM user_roles;
DELETE FROM users;

INSERT INTO users (id, name, email, password, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Mock User', 'mockuser@example.com', 'dummy-password', NOW(), NOW());

INSERT INTO user_roles (user_id, role)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'USER');

INSERT INTO users (id, name, email, password, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440002', 'Forgot Password User', 'forgot@example.com', 'dummy-password', NOW(), NOW());

INSERT INTO user_roles (user_id, role)
VALUES ('550e8400-e29b-41d4-a716-446655440002', 'USER');
