CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================================
-- 0. INSERIR USUÁRIOS
-- =========================================
INSERT INTO users (id, date_created, date_updated, deleted_at, email, password, name) VALUES
('550e8400-e29b-41d4-a716-446655440001', now(), now(), null, 'admin@mecanicadm.com', crypt('Senha123', gen_salt('bf')), 'Administrador')
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'USER')
ON CONFLICT DO NOTHING;

-- =========================================
-- 1. INSERIR CLIENTES
-- =========================================
INSERT INTO clients (id, name, email, document, phone, date_created, date_updated, deleted_at) VALUES
('150b06cf-68b6-455a-94dc-215c9a0f443b', 'Frota ABC Logística', 'contato@abc-logistica.com', '79013046002', '48999999001', now(), now(), null),
('d9426f21-70ab-48d6-847e-4054a3a6b2b7', 'Transportes Silva', 'silva@transportes.com', '10888602065', '48999999002', now(), now(), null),
('4f74d0a8-b649-43c1-a20c-843de26f74a0', 'Táxi Aeroporto', 'taxiaero@mail.com', '93700260024', '48999999003', now(), now(), null),
('6b9cc8de-31a2-4759-9f4a-25091c5c56d1', 'Uber Drivers', 'uber@drivers.com', '46317355000100', '48999999004', now(), now(), null),
('8b3f81e5-e118-4e31-8979-994cba5256e4', 'Deliveries Express', 'express@deliver.com', '46317355000100', '48999999005', now(), now(), null)
ON CONFLICT DO NOTHING;

-- =========================================
-- 2. INSERIR VEÍCULOS
-- =========================================
INSERT INTO vehicle (license_plate, model, brand, model_year, date_created, date_updated, deleted_at) VALUES
('ABC-1234', 'Volvo FH 460 - Caminhão', 'Volvo', 2020, now(), now(), null),
('DEF-5678', 'Scania R470 - Caminhão', 'Scania', 2019, now(), now(), null),
('GHI-9012', 'Mercedes Sprinter - Van', 'Mercedes', 2020, now(), now(), null),
('JKL-3456', 'Ford Transit - Furgão', 'Ford', 2021, now(), now(), null),
('MNO-7890', 'Fiat Ducato - Utilitário', 'Fiat', 2018, now(), now(), null),
('PQR-2345', 'VW Kombi - Kombi', 'Volkswagen', 2022, now(), now(), null),
('STU-6789', 'Chevrolet Opala - Sedan', 'Chevrolet', 1998, now(), now(), null),
('VWX-0123', 'Toyota Corolla - Sedan', 'Toyota', 2017, now(), now(), null),
('YZA-4567', 'Honda Civic - Sedan', 'Honda', 2016, now(), now(), null),
('BCD-8901', 'Renault Master - Van', 'Renault', 2015, now(), now(), null)
ON CONFLICT DO NOTHING;

-- =========================================
-- 3. INSERIR SERVIÇOS (LABORS)
-- =========================================
INSERT INTO labors (id, name, price, date_created, date_updated, deleted_at) VALUES
('7c8449c2-555e-4ef5-b6d4-8395995cfd0a', 'Revisão Completa', 500.00, now(), now(), null),
('1b913e8a-81cf-41c3-bbbd-6cd8342460e7', 'Troca de Óleo', 150.00, now(), now(), null),
('8248fb50-7195-46aa-a3c3-1628d087b2ea', 'Alinhamento de Rodas', 200.00, now(), now(), null),
('f17911cf-9a93-4a1f-a3f1-b93d052a97cf', 'Balanceamento de Rodas', 120.00, now(), now(), null),
('3a95610e-d748-4ff5-b901-21822a101b7a', 'Reparo de Pneu', 80.00, now(), now(), null),
('cc0f225d-0043-4dc9-9801-b7ea38340d21', 'Troca de Filtro', 90.00, now(), now(), null),
('e8281144-411a-4d37-97d8-1e4eb1e79391', 'Limpeza de Carbono', 300.00, now(), now(), null),
('248fa7cf-ab73-455b-8cb8-27e692cfbc40', 'Diagnóstico Eletrônico', 250.00, now(), now(), null)
ON CONFLICT DO NOTHING;

-- =========================================
-- 4. INSERIR MATERIAIS
-- =========================================
INSERT INTO materials (id, name, brand, description, price, type, date_created, date_updated, deleted_at) VALUES
('b3c1b691-8d2b-42ea-a4b5-ea93db622616', 'Óleo 5W30', 'Castrol', 'Óleo sintético de alta performance', 65.00, 'CONSUMABLE', now(), now(), null),
('5c5450e1-236b-43d9-95a7-96a1a1f73b15', 'Filtro de Óleo', 'Mann', 'Filtro de óleo blindado', 45.00, 'PART', now(), now(), null),
('d96a5b9e-b9b5-419b-ab4e-a129ef31ea14', 'Pastilha de Freio', 'Brembo', 'Pastilha cerâmica dianteira', 250.00, 'PART', now(), now(), null),
('ac7f502c-47bc-49b2-be2f-2c35848bb208', 'Limpador de Parabrisa', 'Bosch', 'Palheta aerotwin', 120.00, 'PART', now(), now(), null),
('eab6dfa6-302f-48d1-ba71-eb7f1b72e807', 'Fluido de Freio', 'Varga', 'DOT 4 500ml', 35.00, 'CONSUMABLE', now(), now(), null)
ON CONFLICT DO NOTHING;

-- =========================================
-- 5. INSERIR TRANSAÇÕES DE STOCK
-- =========================================
INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated) VALUES
('a1b1c1d1-e29b-41d4-a716-446655440001', 'b3c1b691-8d2b-42ea-a4b5-ea93db622616', 25, 'ADDITION', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated) VALUES
('a2b2c2d2-e29b-41d4-a716-446655440002', '5c5450e1-236b-43d9-95a7-96a1a1f73b15', 25, 'ADDITION', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated) VALUES
('a3b3c3d3-e29b-41d4-a716-446655440003', 'd96a5b9e-b9b5-419b-ab4e-a129ef31ea14', 25, 'ADDITION', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated) VALUES
('a4b4c4d4-e29b-41d4-a716-446655440004', 'ac7f502c-47bc-49b2-be2f-2c35848bb208', 25, 'ADDITION', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, quantity, type, date_created, date_updated) VALUES
('a5b5c5d5-e29b-41d4-a716-446655440005', 'eab6dfa6-302f-48d1-ba71-eb7f1b72e807', 25, 'ADDITION', now(), now())
ON CONFLICT DO NOTHING;

-- =========================================
-- 6. INSERIR ORDENS DE SERVIÇO
-- =========================================

-- =========================================
-- Criando work order 1: Troca de óleo (finalizada)
-- =========================================
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, execution_end_at, date_created, date_updated)
VALUES
('aa11aa11-1111-4aa1-8a11-aaaaaaaa1111', '150b06cf-68b6-455a-94dc-215c9a0f443b', 'ABC-1234', 'Troca de óleo completa e verificação geral', 4, now() - interval '2 hours', now() - interval '30 minutes', now() - interval '3 hours', now() - interval '30 minutes')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity) VALUES
('aa11aa11-2222-4aa1-8a11-aaaaaaaa2222', 'aa11aa11-1111-4aa1-8a11-aaaaaaaa1111', 'b3c1b691-8d2b-42ea-a4b5-ea93db622616', 2),
('aa11aa11-3333-4aa1-8a11-aaaaaaaa3333', 'aa11aa11-1111-4aa1-8a11-aaaaaaaa1111', '5c5450e1-236b-43d9-95a7-96a1a1f73b15', 1)
ON CONFLICT DO NOTHING;

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status, execution_start_at, execution_end_at) VALUES
('aa11aa11-4444-4aa1-8a11-aaaaaaaa4444', 'aa11aa11-1111-4aa1-8a11-aaaaaaaa1111', '1b913e8a-81cf-41c3-bbbd-6cd8342460e7', 'EXECUTION_COMPLETED', now() - interval '2 hours', now() - interval '45 minutes')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_budgets (work_order_id, total_price, status) VALUES
('aa11aa11-1111-4aa1-8a11-aaaaaaaa1111', 325.00, 'APPROVED')
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated) VALUES
('aa11aa11-5555-4aa1-8a11-aaaaaaaa5555', 'b3c1b691-8d2b-42ea-a4b5-ea93db622616', 'aa11aa11-1111-4aa1-8a11-aaaaaaaa1111', 2, 'REDUCTION', now() - interval '2 hours', now() - interval '2 hours'),
('aa11aa11-6666-4aa1-8a11-aaaaaaaa6666', '5c5450e1-236b-43d9-95a7-96a1a1f73b15', 'aa11aa11-1111-4aa1-8a11-aaaaaaaa1111', 1, 'REDUCTION', now() - interval '2 hours', now() - interval '2 hours')
ON CONFLICT DO NOTHING;

-- =========================================
-- Criando work order 2: Alinhamento (em execução)
-- =========================================
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, date_created, date_updated)
VALUES
('bb22bb22-2222-4bb2-8b22-bbbbbbbbbbbb', 'd9426f21-70ab-48d6-847e-4054a3a6b2b7', 'DEF-5678', 'Alinhamento e balanceamento', 0, now() - interval '1 hours', now() - interval '1 hours', now())
ON CONFLICT DO NOTHING;

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status, execution_start_at) VALUES
('bb22bb22-3333-4bb2-8b22-bbbbbbbbbbbb', 'bb22bb22-2222-4bb2-8b22-bbbbbbbbbbbb', '8248fb50-7195-46aa-a3c3-1628d087b2ea', 'IN_EXECUTION', now() - interval '1 hours')
ON CONFLICT DO NOTHING;

-- =========================================
-- Criando work order 3: Diagnóstico (recebida) com material listado
-- =========================================
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES
('cc33cc33-3333-4cc3-8c33-cccccccccccc', '4f74d0a8-b649-43c1-a20c-843de26f74a0', 'GHI-9012', 'Diagnóstico de ruído ao frear (possível troca de pastilhas)', 3, now() - interval '3 days', now() - interval '3 days')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity) VALUES
('cc33cc33-4444-4cc3-8c33-cccccccccccc', 'cc33cc33-3333-4cc3-8c33-cccccccccccc', 'd96a5b9e-b9b5-419b-ab4e-a129ef31ea14', 4)
ON CONFLICT DO NOTHING;

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status) VALUES
('cc33cc33-5555-4cc3-8c33-cccccccccccc', 'cc33cc33-3333-4cc3-8c33-cccccccccccc', '3a95610e-d748-4ff5-b901-21822a101b7a', 'AWAITING_EXECUTION')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_budgets (work_order_id, total_price, status) VALUES
('cc33cc33-3333-4cc3-8c33-cccccccccccc', 1080.00, 'PENDING')
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated) VALUES
('cc33cc33-7777-4cc3-8c33-cccccccc7777', 'd96a5b9e-b9b5-419b-ab4e-a129ef31ea14', 'cc33cc33-3333-4cc3-8c33-cccccccccccc', 4, 'REDUCTION', now() - interval '1 days', now() - interval '1 days')
ON CONFLICT DO NOTHING;

-- =========================================
-- Criando work order 4: Revisão completa (orçamento enviado / aguardando decisão)
-- =========================================
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES
('dd44dd44-4444-4dd4-8d44-dddddddddddd', '6b9cc8de-31a2-4759-9f4a-25091c5c56d1', 'JKL-3456', 'Serviço de revisão e limpeza de carbono', 1, now() - interval '4 days', now() - interval '4 days')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status) VALUES
('dd44dd44-5555-4dd4-8d44-dddddddddddd', 'dd44dd44-4444-4dd4-8d44-dddddddddddd', '7c8449c2-555e-4ef5-b6d4-8395995cfd0a', 'AWAITING_EXECUTION')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity) VALUES
('dd44dd44-6666-4dd4-8d44-dddddddddddd', 'dd44dd44-4444-4dd4-8d44-dddddddddddd', 'eab6dfa6-302f-48d1-ba71-eb7f1b72e807', 1)
ON CONFLICT DO NOTHING;

INSERT INTO work_order_budgets (work_order_id, total_price, status) VALUES
('dd44dd44-4444-4dd4-8d44-dddddddddddd', 335.00, 'WAITING_DECISION')
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated) VALUES
('dd44dd44-7777-4dd4-8d44-dddddddddd77', 'eab6dfa6-302f-48d1-ba71-eb7f1b72e807', 'dd44dd44-4444-4dd4-8d44-dddddddddddd', 1, 'REDUCTION', now() - interval '4 days', now() - interval '4 days')
ON CONFLICT DO NOTHING;

-- =========================================
-- Criando work order 5: Serviço com peças (recebida)
-- =========================================
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, date_created, date_updated)
VALUES
('ee55ee55-5555-4ee5-8e55-eeeeeeeeeeee', '8b3f81e5-e118-4e31-8979-994cba5256e4', 'PQR-2345', 'Reparo e substituição de palhetas e inspeção', 3, now() - interval '2 days', now() - interval '2 days')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity) VALUES
('ee55ee55-6666-4ee5-8e55-eeeeeeee6666', 'ee55ee55-5555-4ee5-8e55-eeeeeeeeeeee', 'ac7f502c-47bc-49b2-be2f-2c35848bb208', 2)
ON CONFLICT DO NOTHING;

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status) VALUES
('ee55ee55-7777-4ee5-8e55-eeeeeeee7777', 'ee55ee55-5555-4ee5-8e55-eeeeeeeeeeee', 'cc0f225d-0043-4dc9-9801-b7ea38340d21', 'AWAITING_EXECUTION')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_budgets (work_order_id, total_price, status) VALUES
('ee55ee55-5555-4ee5-8e55-eeeeeeeeeeee', 330.00, 'PENDING')
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated) VALUES
('ee55ee55-8888-4ee5-8e55-eeeeeeee8888', 'ac7f502c-47bc-49b2-be2f-2c35848bb208', 'ee55ee55-5555-4ee5-8e55-eeeeeeeeeeee', 2, 'REDUCTION', now() - interval '2 days', now() - interval '2 days')
ON CONFLICT DO NOTHING;


-- =========================================
-- Criando work order 6: Reparo de freios (finalizada)
-- =========================================
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, execution_end_at, date_created, date_updated)
VALUES
('ff66ff66-6666-4ff6-8f66-ffffffff6666', '8b3f81e5-e118-4e31-8979-994cba5256e4', 'PQR-2345', 'Substituição de pastilhas de freio e teste de frenagem', 4, now() - interval '14 days', now() - interval '10 days', now() - interval '14 days', now() - interval '10 days')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity) VALUES
('ff66ff66-7777-4ff6-8f66-ffffffff7777', 'ff66ff66-6666-4ff6-8f66-ffffffff6666', 'd96a5b9e-b9b5-419b-ab4e-a129ef31ea14', 4)
ON CONFLICT DO NOTHING;

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status, execution_start_at, execution_end_at) VALUES
('ff66ff66-8888-4ff6-8f66-ffffffff8888', 'ff66ff66-6666-4ff6-8f66-ffffffff6666', '3a95610e-d748-4ff5-b901-21822a101b7a', 'EXECUTION_COMPLETED', now() - interval '14 days', now() - interval '10 days')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_budgets (work_order_id, total_price, status) VALUES
('ff66ff66-6666-4ff6-8f66-ffffffff6666', 1080.00, 'APPROVED')
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated) VALUES
('ff66ff66-9999-4ff6-8f66-ffffffff9999', 'd96a5b9e-b9b5-419b-ab4e-a129ef31ea14', 'ff66ff66-6666-4ff6-8f66-ffffffff6666', 4, 'REDUCTION', now() - interval '14 days', now() - interval '14 days')
ON CONFLICT DO NOTHING;

-- =========================================
-- Criando work order 7: Revisão completa (finalizada)
-- =========================================
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, execution_end_at, date_created, date_updated)
VALUES
('66776677-7777-4667-8677-666666667777', '4f74d0a8-b649-43c1-a20c-843de26f74a0', 'GHI-9012', 'Revisão completa e limpeza de carbono', 4, now() - interval '3 hours', now() - interval '2 hours', now() - interval '4 hours', now() - interval '2 hours')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity) VALUES
('66776677-8888-4667-8677-666666668888', '66776677-7777-4667-8677-666666667777', 'eab6dfa6-302f-48d1-ba71-eb7f1b72e807', 1),
('66776677-9999-4667-8677-666666669999', '66776677-7777-4667-8677-666666667777', 'b3c1b691-8d2b-42ea-a4b5-ea93db622616', 1)
ON CONFLICT DO NOTHING;

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status, execution_start_at, execution_end_at) VALUES
('66776677-aaaa-4667-8677-66666666aaaa', '66776677-7777-4667-8677-666666667777', '7c8449c2-555e-4ef5-b6d4-8395995cfd0a', 'EXECUTION_COMPLETED', now() - interval '3 hours', now() - interval '2 hours')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_budgets (work_order_id, total_price, status) VALUES
('66776677-7777-4667-8677-666666667777', 600.00, 'APPROVED')
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated) VALUES
('66776677-bbbb-4667-8677-66666666bbbb', 'eab6dfa6-302f-48d1-ba71-eb7f1b72e807', '66776677-7777-4667-8677-666666667777', 1, 'REDUCTION', now() - interval '3 hours', now() - interval '3 hours'),
('66776677-cccc-4667-8677-66666666cccc', 'b3c1b691-8d2b-42ea-a4b5-ea93db622616', '66776677-7777-4667-8677-666666667777', 1, 'REDUCTION', now() - interval '3 hours', now() - interval '3 hours')
ON CONFLICT DO NOTHING;

-- =========================================
-- Criando work order 8: Troca de óleo e filtro (finalizada)
-- =========================================
INSERT INTO work_orders (id, client_id, vehicle_id, description, status, execution_start_at, execution_end_at, date_created, date_updated)
VALUES
('88886688-8888-4888-8888-888888888888', 'd9426f21-70ab-48d6-847e-4054a3a6b2b7', 'DEF-5678', 'Troca de óleo e filtro, verificação de vazamentos', 4, now() - interval '1 day', now() - interval '23 hours', now() - interval '1 day', now() - interval '23 hours')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_material_items (id, work_order_id, material_id, quantity) VALUES
('88886688-9999-4888-8888-888888889999', '88886688-8888-4888-8888-888888888888', 'b3c1b691-8d2b-42ea-a4b5-ea93db622616', 1),
('88886688-aaaa-4888-8888-8888888888aa', '88886688-8888-4888-8888-888888888888', '5c5450e1-236b-43d9-95a7-96a1a1f73b15', 1)
ON CONFLICT DO NOTHING;

INSERT INTO work_order_labor_items (id, work_order_id, labor_id, status, execution_start_at, execution_end_at) VALUES
('88886688-bbbb-4888-8888-8888888888bb', '88886688-8888-4888-8888-888888888888', '1b913e8a-81cf-41c3-bbbd-6cd8342460e7', 'EXECUTION_COMPLETED', now() - interval '1 day', now() - interval '23 hours')
ON CONFLICT DO NOTHING;

INSERT INTO work_order_budgets (work_order_id, total_price, status) VALUES
('88886688-8888-4888-8888-888888888888', 175.00, 'APPROVED')
ON CONFLICT DO NOTHING;

INSERT INTO stock_movements (id, material_id, work_order_id, quantity, type, date_created, date_updated) VALUES
('88886688-cccc-4888-8888-8888888888cc', 'b3c1b691-8d2b-42ea-a4b5-ea93db622616', '88886688-8888-4888-8888-888888888888', 1, 'REDUCTION', now() - interval '1 day', now() - interval '1 day'),
('88886688-dddd-4888-8888-8888888888dd', '5c5450e1-236b-43d9-95a7-96a1a1f73b15', '88886688-8888-4888-8888-888888888888', 1, 'REDUCTION', now() - interval '1 day', now() - interval '1 day')
ON CONFLICT DO NOTHING;
