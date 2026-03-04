-- Script SQL para crear datos de prueba para el endpoint /v1/graph/metrics
-- Asegurarse de que la tabla movements tiene la estructura correcta

-- Tabla movements (si no existe)
CREATE TABLE IF NOT EXISTS movements (
    id SERIAL PRIMARY KEY,
    attempt_id UUID NOT NULL,
    step INT NOT NULL,
    state_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices (si no existen)
CREATE INDEX IF NOT EXISTS idx_movements_attempt_step
    ON movements(attempt_id, step);

CREATE INDEX IF NOT EXISTS idx_movements_attempt_state_hash
    ON movements(attempt_id, state_hash);

-- Limpiar datos de prueba anteriores
DELETE FROM movements WHERE attempt_id IN (
    'test-uuid-123',
    '8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999'
);

-- Insertar datos de prueba para el test case del spec
-- attempt_id: 8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999
-- Simula 20 movimientos con 15 estados únicos (6 estados repetidos)
-- Cyclicity esperada = 6 / 20 = 0.3

-- Step 0: Estado inicial (INIT)
INSERT INTO movements (attempt_id, step, state_hash) VALUES
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 0, 'hash_state_init');

-- Steps 1-20: Movimientos con algunos estados repetidos
INSERT INTO movements (attempt_id, step, state_hash) VALUES
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 1, 'hash_state_1'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 2, 'hash_state_2'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 3, 'hash_state_3'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 4, 'hash_state_4'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 5, 'hash_state_5'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 6, 'hash_state_6'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 7, 'hash_state_7'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 8, 'hash_state_8'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 9, 'hash_state_9'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 10, 'hash_state_10'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 11, 'hash_state_11'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 12, 'hash_state_12'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 13, 'hash_state_13'),
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 14, 'hash_state_14'),
-- Estados repetidos (6 repeticiones)
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 15, 'hash_state_3'),  -- Repetición de step 3
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 16, 'hash_state_5'),  -- Repetición de step 5
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 17, 'hash_state_7'),  -- Repetición de step 7
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 18, 'hash_state_9'),  -- Repetición de step 9
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 19, 'hash_state_11'), -- Repetición de step 11
('8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999', 20, 'hash_state_13'); -- Repetición de step 13

-- Verificar los datos insertados
SELECT
    COUNT(*) - 1 AS total_moves,
    COUNT(*)     AS visited_states,
    COUNT(DISTINCT state_hash) AS unique_states,
    (COUNT(*) - COUNT(DISTINCT state_hash)) AS repeated_states,
    CASE
        WHEN COUNT(*) - 1 = 0 THEN 0
        ELSE (COUNT(*) - COUNT(DISTINCT state_hash))::float / (COUNT(*) - 1)
    END AS cyclicity
FROM movements
WHERE attempt_id = '8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999';

-- Resultado esperado:
-- total_moves: 20
-- visited_states: 21
-- unique_states: 15 (init + 14 estados únicos)
-- repeated_states: 6
-- cyclicity: 0.3

-- Caso de prueba adicional: Solo estado inicial (M=0)
INSERT INTO movements (attempt_id, step, state_hash) VALUES
('test-uuid-only-init', 0, 'hash_initial_only');

-- Verificar
SELECT
    COUNT(*) - 1 AS total_moves,
    CASE
        WHEN COUNT(*) - 1 = 0 THEN 0
        ELSE (COUNT(*) - COUNT(DISTINCT state_hash))::float / (COUNT(*) - 1)
    END AS cyclicity
FROM movements
WHERE attempt_id = 'test-uuid-only-init';
-- Resultado esperado: total_moves = 0, cyclicity = 0
