DELETE FROM password_reset_tokens;

INSERT INTO password_reset_tokens (id, token, user_id, expiry_date)
VALUES (gen_random_uuid(), 'expired-reset-token', '550e8400-e29b-41d4-a716-446655440001', NOW() - INTERVAL '10 minutes');
