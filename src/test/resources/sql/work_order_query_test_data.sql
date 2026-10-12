INSERT INTO clients (id, name, email, document, phone, date_created, date_updated, deleted_at)
VALUES ('660e8400-e29b-41d4-a716-446655440300', 'João Silva', 'joao.silva@test.com', '12345678901', '48999999911',
        now(), now(), null),
       ('660e8400-e29b-41d4-a716-446655440301', 'Maria Santos', 'maria.santos@test.com', '98765432100', '48888888888',
        now(), now(), null);

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated, deleted_at)
VALUES ('GET-1234', 'Civic', 'Honda', 2023, now(), now(), null),
       ('GET-5678', 'Corolla', 'Toyota', 2022, now(), now(), null);

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated, deleted_at)
VALUES ('660e8400-e29b-41d4-a716-446655440400', '660e8400-e29b-41d4-a716-446655440300', 'GET-1234', 'Troca de óleo',
        3, now(), now(), null),
       ('660e8400-e29b-41d4-a716-446655440401', '660e8400-e29b-41d4-a716-446655440301', 'GET-5678', 'Revisão geral',
        2, now(), now(), null);

INSERT INTO materials (id, name, brand, description, price, type, date_created, date_updated, deleted_at)
VALUES ('660e8400-e29b-41d4-a716-446655440402', 'Óleo de Motor 5W30', 'Castrol', 'Óleo sintético para motor', 50.00,
        'CONSUMABLE', now(), now(), null);

INSERT INTO work_order_budgets (work_order_id, total_price, status, observation)
VALUES ('660e8400-e29b-41d4-a716-446655440400', 150.00, 'APPROVED', null);

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity)
VALUES (gen_random_uuid(), '660e8400-e29b-41d4-a716-446655440400', '660e8400-e29b-41d4-a716-446655440402', 2);
