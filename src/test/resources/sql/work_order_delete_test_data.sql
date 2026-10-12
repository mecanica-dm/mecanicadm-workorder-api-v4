INSERT INTO clients (id, name, email, document, phone, date_created, date_updated, deleted_at)
VALUES ('770e8400-e29b-41d4-a716-446655440300', 'Cliente para Deleção', 'delete.client@test.com', '78945612301',
        '48777777777', now(), now(), null);

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated, deleted_at)
VALUES ('DEL-1234', 'Deletable Car', 'Toyota', 2020, now(), now(), null);

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated, deleted_at)
VALUES ('770e8400-e29b-41d4-a716-446655440400', '770e8400-e29b-41d4-a716-446655440300', 'DEL-1234',
        'Ordem de serviço para deletar', 3, now(), now(), null);
