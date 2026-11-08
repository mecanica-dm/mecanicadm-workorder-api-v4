INSERT INTO clients (id, name, email, document, phone, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440001', 'Cliente Diagnostico', 'cliente.diagnostico@test.com', '98765432100',
        '48999999900', now(), now());

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated)
VALUES ('ZZZ-9999', 'Onix LT', 'Chevrolet', 2020, now(), now());

INSERT INTO labors (id, name, price, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440010', 'Revisao eletrica', 150.00, now(), now());

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440020', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999',
        'Ordem de Serviço com labor para diagnosticar', 3, now(), now()),
       ('660e8400-e29b-41d4-a716-446655440021', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999',
        'Ordem de Serviço sem labor para validar regra', 3, now(), now());

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status)
VALUES ('660e8400-e29b-41d4-a716-446655440030', '660e8400-e29b-41d4-a716-446655440020',
        '660e8400-e29b-41d4-a716-446655440010', 'AWAITING_EXECUTION');
