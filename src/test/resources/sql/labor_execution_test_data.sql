INSERT INTO clients (id, name, email, document, phone, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440001', 'Cliente Execucao', 'cliente.execucao@test.com', '98765432100',
        '48999999900', now(), now());

INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated)
VALUES ('ZZZ-9999', 'Onix LT', 'Chevrolet', 2020, now(), now());

INSERT INTO labors (id, name, price, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440020', 'Troca de Oleo', 100.00, now(), now());

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440090', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999',
        'Ordem de serviço para iniciar', 1, now(), now());

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status)
VALUES ('660e8400-e29b-41d4-a716-446655440085', '660e8400-e29b-41d4-a716-446655440090',
        '660e8400-e29b-41d4-a716-446655440020', 'AWAITING_EXECUTION');

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440091', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999',
        'Ordem de serviço em execução com labor pendente', 0, now(), now(), now());

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status)
VALUES ('660e8400-e29b-41d4-a716-446655440086', '660e8400-e29b-41d4-a716-446655440091',
        '660e8400-e29b-41d4-a716-446655440020', 'AWAITING_EXECUTION');

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440092', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999',
        'Ordem de serviço com labor rodando', 0, now(), now(), now());

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status, execution_start_at)
VALUES ('660e8400-e29b-41d4-a716-446655440087', '660e8400-e29b-41d4-a716-446655440092',
        '660e8400-e29b-41d4-a716-446655440020', 'IN_EXECUTION', now());

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440093', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999',
        'Ordem de serviço pronta para finalizar', 0, now(), now(), now());

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status, execution_start_at, execution_end_at)
VALUES ('660e8400-e29b-41d4-a716-446655440088', '660e8400-e29b-41d4-a716-446655440093',
        '660e8400-e29b-41d4-a716-446655440020', 'EXECUTION_COMPLETED', now(), now());

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, execution_end_at,
                         date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440094', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999',
        'Ordem de serviço pronta para pagamento', 4, now(), now(), now(), now());

INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, execution_end_at,
                         date_created, date_updated)
VALUES ('660e8400-e29b-41d4-a716-446655440095', '660e8400-e29b-41d4-a716-446655440001', 'ZZZ-9999',
        'Ordem de serviço paga pronta para retirada', 5, now(), now(), now(), now());
