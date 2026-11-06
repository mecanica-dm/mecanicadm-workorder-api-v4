INSERT INTO clients (id, name, email, document, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'Test Client', 'test@example.com', '12345678900', NOW(), NOW());

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated)
VALUES ('ABC-1234', 'Civic', 'Honda', 2020, NOW(), NOW());

INSERT INTO materials (id, name, price, type, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Óleo Motor', 50.00, 'CONSUMABLE', NOW(), NOW());
INSERT INTO materials (id, name, price, type, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440002', 'Filtro de Óleo', 30.00, 'PART', NOW(), NOW());

INSERT INTO labors (id, name, price, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440003', 'Troca de Óleo', 80.00, NOW(), NOW());
INSERT INTO labors (id, name, price, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440004', 'Alinhamento', 120.00, NOW(), NOW());

INSERT INTO work_orders (id, client_id, vehicle_id, status, description, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440000', 'ABC-1234', 3,
        'Troca de óleo e alinhamento', NOW(), NOW());

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity)
VALUES (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', 2);
INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity)
VALUES (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440002', 1);

INSERT INTO work_order_labor_items (id, work_order_id, status, labor_id)
VALUES (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440005', 'AWAITING_EXECUTION',
        '550e8400-e29b-41d4-a716-446655440003');
INSERT INTO work_order_labor_items (id, work_order_id, status, labor_id)
VALUES (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440005', 'AWAITING_EXECUTION',
        '550e8400-e29b-41d4-a716-446655440004');
