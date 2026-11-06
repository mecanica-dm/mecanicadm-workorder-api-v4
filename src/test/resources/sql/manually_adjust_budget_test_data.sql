INSERT INTO clients (id, name, email, document, phone, date_created, date_updated, deleted_at)
VALUES ('660e8400-e29b-41d4-a716-446655440001', 'Cliente Budget', 'cliente.budget@test.com', '98765432100',
        '48999999900', now(), now(), null);

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated, deleted_at)
VALUES ('ZZZ-9999', 'Onix LT', 'Chevrolet', 2020, now(), now(), null);

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated, deleted_at)
VALUES ('660e8400-e29b-41d4-a716-446655440050', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999', 'OS com budget',
        2, now(), now(), null);

INSERT INTO work_order_budgets (work_order_id, total_price, status)
VALUES ('660e8400-e29b-41d4-a716-446655440050', 500.00, 'PENDING');
