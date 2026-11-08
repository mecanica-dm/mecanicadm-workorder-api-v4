INSERT INTO materials (id, name, price, type, date_created, date_updated)
VALUES ('770e8400-e29b-41d4-a716-446655440001', 'Material Teste 1', 10.00, 'PART', NOW(), NOW());

INSERT INTO materials (id, name, price, type, date_created, date_updated)
VALUES ('990e8400-e29b-41d4-a716-446655449999', 'Material Teste 2', 20.00, 'PART', NOW(), NOW());

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated)
VALUES (gen_random_uuid(), '770e8400-e29b-41d4-a716-446655440001', 10, 'ADDITION', NOW(), NOW());

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated)
VALUES (gen_random_uuid(), '770e8400-e29b-41d4-a716-446655440001', 5, 'ADDITION', NOW(), NOW());

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated)
VALUES (gen_random_uuid(), '770e8400-e29b-41d4-a716-446655440001', 3, 'REDUCTION', NOW(), NOW());

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated)
VALUES (gen_random_uuid(), '770e8400-e29b-41d4-a716-446655440001', 2, 'REDUCTION', NOW(), NOW());

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated)
VALUES (gen_random_uuid(), '990e8400-e29b-41d4-a716-446655449999', 50, 'ADDITION', NOW(), NOW());
