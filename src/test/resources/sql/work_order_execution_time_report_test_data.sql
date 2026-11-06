INSERT INTO clients (id, name, email, document, phone, date_created, date_updated, deleted_at)
VALUES ('960e8400-e29b-41d4-a716-446655440001', 'Cliente Relatorio', 'cliente.relatorio@test.com', '12312312312',
        '48999990000', now(), now(), null);

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated, deleted_at)
VALUES ('REL-1234', 'HB20', 'Hyundai', 2020, now(), now(), null);

INSERT INTO labors (id, name, price, date_created, date_updated, deleted_at)
VALUES ('960e8400-e29b-41d4-a716-446655440011', 'Mao de obra 1', 100.00, now(), now(), null),
       ('960e8400-e29b-41d4-a716-446655440012', 'Mao de obra 2', 150.00, now(), now(), null);

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, execution_end_at,
                         date_created, date_updated, deleted_at)
VALUES ('960e8400-e29b-41d4-a716-446655440101', '960e8400-e29b-41d4-a716-446655440001', 'REL-1234', 'OS rapida',
        4, TIMESTAMP '2026-01-10 08:00:00', TIMESTAMP '2026-01-10 09:00:00', now(), now(), null),
       ('960e8400-e29b-41d4-a716-446655440102', '960e8400-e29b-41d4-a716-446655440001', 'REL-1234', 'OS lenta',
        4, TIMESTAMP '2026-01-12 08:00:00', TIMESTAMP '2026-01-12 12:00:00', now(), now(), null),
       ('960e8400-e29b-41d4-a716-446655440103', '960e8400-e29b-41d4-a716-446655440001', 'REL-1234', 'OS em andamento',
        0, TIMESTAMP '2026-01-15 08:00:00', null, now(), now(), null);

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status, execution_start_at, execution_end_at)
VALUES ('960e8400-e29b-41d4-a716-446655440201', '960e8400-e29b-41d4-a716-446655440101',
        '960e8400-e29b-41d4-a716-446655440011', 'EXECUTION_COMPLETED', TIMESTAMP '2026-01-10 08:00:00',
        TIMESTAMP '2026-01-10 08:30:00'),
       ('960e8400-e29b-41d4-a716-446655440202', '960e8400-e29b-41d4-a716-446655440102',
        '960e8400-e29b-41d4-a716-446655440012', 'EXECUTION_COMPLETED', TIMESTAMP '2026-01-12 08:00:00',
        TIMESTAMP '2026-01-12 10:00:00'),
       ('960e8400-e29b-41d4-a716-446655440203', '960e8400-e29b-41d4-a716-446655440103',
        '960e8400-e29b-41d4-a716-446655440012', 'IN_EXECUTION', TIMESTAMP '2026-01-15 08:00:00', null);

