INSERT INTO clients (id, name, email, document, phone, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440001', 'Cliente Recalculo', 'cliente.recalculo@test.com', '98765432100',
        '48999999900', now(), now());

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated)
VALUES ('ZZZ-9999', 'Onix LT', 'Chevrolet', 2020, now(), now());

INSERT INTO materials (id, name, price, type, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440010', 'Oleo Motor', 10, 'PART', now(), now());

INSERT INTO labors (id, name, price, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440020', 'Troca de Oleo', 100.00, now(), now());

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated, deleted_at)
VALUES ('660e8400-e29b-41d4-a716-446655440060', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999', 'OS para recalculo',
        2, now(), now(), null);

INSERT INTO work_order_budgets (work_order_id, total_price, status)
VALUES ('660e8400-e29b-41d4-a716-446655440060', 50.00, 'PENDING');

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity)
VALUES ('660e8400-e29b-41d4-a716-446655440070', '660e8400-e29b-41d4-a716-446655440060',
        '660e8400-e29b-41d4-a716-446655440010', 2);

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status)
VALUES ('660e8400-e29b-41d4-a716-446655440080', '660e8400-e29b-41d4-a716-446655440060',
        '660e8400-e29b-41d4-a716-446655440020', 'AWAITING_EXECUTION');
