DELETE FROM inbox;
DELETE FROM outbox;
DELETE FROM stock_movements;
DELETE FROM work_order_material_items;
DELETE FROM work_order_labor_items;
DELETE FROM work_orders;
DELETE FROM materials;
DELETE FROM labors;
DELETE FROM vehicle;
DELETE FROM clients;

INSERT INTO clients (id, name, email, document, phone, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440001', 'Cliente Saga', 'cliente.saga@test.com', '11122233344',
        '48999999911', now(), now());

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated)
VALUES ('SAG-0001', 'Onix LT', 'Chevrolet', 2020, now(), now());

INSERT INTO labors (id, name, price, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440010', 'Troca de oleo', 80.00, now(), now()),
       ('770e8400-e29b-41d4-a716-446655440011', 'Alinhamento', 120.00, now(), now());

INSERT INTO materials (id, name, brand, description, price, type, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440020', 'Oleo 5W30', 'Brand X', 'Oleo sintetico', 50.00, 'PART', now(), now());

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated)
VALUES (gen_random_uuid(), '770e8400-e29b-41d4-a716-446655440020', 100, 'ADDITION', now(), now());

-- status (ordinal): 0 IN_EXECUTION, 2 DIAGNOSED, 3 RECEIVED
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440100', '770e8400-e29b-41d4-a716-446655440001', 'SAG-0001', 'OS recebida', 3, now(), now()),
       ('770e8400-e29b-41d4-a716-446655440101', '770e8400-e29b-41d4-a716-446655440001', 'SAG-0001', 'OS em execucao', 0, now(), now()),
       ('770e8400-e29b-41d4-a716-446655440102', '770e8400-e29b-41d4-a716-446655440001', 'SAG-0001', 'OS diagnosticada', 2, now(), now());
