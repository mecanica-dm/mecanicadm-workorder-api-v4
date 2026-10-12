INSERT INTO clients (id, name, email, document, date_created, date_updated)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Cliente Precondition', 'precondition@test.com', '12345678902', NOW(), NOW());

INSERT INTO vehicle (license_plate, model, date_created, date_updated)
VALUES ('ABC-1234', 'Teste Model', NOW(), NOW());

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440090', '550e8400-e29b-41d4-a716-446655440001', 'ABC-1234', 'OS já diagnosticada', 2, NOW(), NOW());

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440091', '550e8400-e29b-41d4-a716-446655440001', 'ABC-1234', 'OS sem veículo', 3, NOW(), NOW());
