INSERT INTO clients (id, name, email, document, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440999', 'Decide Client', 'decide@test.com', '12345678903', NOW(), NOW());

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated)
VALUES ('ABC-1234', 'Decide Model', 'Decide Brand', 2023, NOW(), NOW());

INSERT INTO materials (id, name, price, type, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440001', 'Óleo Motor', 50.00, 'CONSUMABLE', NOW(), NOW());

INSERT INTO labors (id, name, price, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440003', 'Troca de Óleo', 80.00, NOW(), NOW());

INSERT INTO work_orders (id, client_id, vehicle_id, status, description, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440010', '770e8400-e29b-41d4-a716-446655440999', 'ABC-1234', 2,
        'Decisão de orçamento', NOW(), NOW());

INSERT INTO work_order_budgets (work_order_id, total_price, status)
VALUES ('770e8400-e29b-41d4-a716-446655440010', 130.00, 'WAITING_DECISION');
