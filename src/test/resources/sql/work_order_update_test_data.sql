INSERT INTO clients (id, name, email, document, phone, date_created, date_updated, deleted_at)
VALUES ('660e8400-e29b-41d4-a716-446655440100', 'Cliente Update', 'cliente.update@test.com', '12345678901',
        '48999999911', now(), now(), null);

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated, deleted_at)
VALUES ('UPD-1234', 'Civic', 'Honda', 2020, now(), now(), null);

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated, deleted_at)
VALUES ('660e8400-e29b-41d4-a716-446655440200', '660e8400-e29b-41d4-a716-446655440100', 'UPD-1234',
        'Descrição original', 3, now(), now(), null),
       ('660e8400-e29b-41d4-a716-446655440201', '660e8400-e29b-41d4-a716-446655440100', 'UPD-1234',
        'Descrição Ordem de serviço diagnosticada', 2, now(), now(), null);
