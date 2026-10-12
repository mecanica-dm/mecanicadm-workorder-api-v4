INSERT INTO materials (id, name, description, brand, type, price, date_created, date_updated)
VALUES ('11111111-1111-1111-1111-111111111111', 'Filtro de Óleo', 'Filtro para motor', 'Bosch', 'CONSUMABLE', 25.50,
        '2023-01-01 09:00:00', '2023-01-01 09:00:00');

INSERT INTO clients (id, name, email, document, phone, date_created, date_updated)
VALUES ('55555555-5555-5555-5555-555555555555', 'Cliente Teste', 'teste@teste.com', '12345678901', '11999999999',
        '2023-01-01 09:00:00', '2023-01-01 09:00:00');

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated)
VALUES ('ABC1234', 'Fusca', 'Volkswagen', 1969, '2023-01-01 09:00:00', '2023-01-01 09:00:00');

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES ('44444444-4444-4444-4444-444444444444', '55555555-5555-5555-5555-555555555555', 'ABC1234', 'Troca de oleo',
        3, '2023-01-01 09:00:00', '2023-01-01 09:00:00');

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated)
VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 10, 'ADDITION',
        '2023-01-01 10:00:00', '2023-01-01 10:00:00');

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated)
VALUES ('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111',
        '44444444-4444-4444-4444-444444444444', 3, 'REDUCTION', '2023-01-02 10:00:00', '2023-01-02 10:00:00');
