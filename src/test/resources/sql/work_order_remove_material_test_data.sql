INSERT INTO clients (id, name, email, phone, document, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440098', 'Cliente Teste Material', 'material@example.com', '11999999999',
        '12345678900', '2023-01-01 10:00:00', '2023-01-01 10:00:00');

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated)
VALUES ('ADD-MAT', 'Test Model', 'Test Brand', 2020, '2023-01-01 10:00:00', '2023-01-01 10:00:00');

INSERT INTO materials (id, name, brand, description, price, type, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440097', 'Óleo do Motor', 'Brand X', 'Óleo sintético 5W-30', 50.00, 'PART',
        '2023-01-01 10:00:00', '2023-01-01 10:00:00');

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440412', '660e8400-e29b-41d4-a716-446655440098', 'ADD-MAT',
        'Ordem de serviço para adicionar material', 3, '2023-01-01 10:00:00', '2023-01-01 10:00:00');

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity)
VALUES ('660e8400-e29b-41d4-a716-446655440099', '660e8400-e29b-41d4-a716-446655440412',
        '660e8400-e29b-41d4-a716-446655440097', 1);

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440100', '660e8400-e29b-41d4-a716-446655440097',
        '660e8400-e29b-41d4-a716-446655440412', 1, 'REDUCTION', '2023-01-01 10:00:00', '2023-01-01 10:00:00');