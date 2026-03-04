#!/bin/bash

# Script de prueba para endpoint /v1/graph/metrics

BASE_URL="http://localhost:8080"

echo "==================================="
echo "Test 1: Branching N=3 inicial"
echo "==================================="
curl -X POST "${BASE_URL}/v1/graph/metrics" \
  -H "Content-Type: application/json" \
  -d '{
    "attempt_id": "test-uuid-123",
    "state": [1, 2, 3, 0, 4, 5, 6],
    "options": {
      "return_meta": true
    }
  }' | jq .

echo -e "\n==================================="
echo "Test 2: Estado inválido (sin cero)"
echo "==================================="
curl -X POST "${BASE_URL}/v1/graph/metrics" \
  -H "Content-Type: application/json" \
  -d '{
    "attempt_id": "test-uuid-123",
    "state": [1, 2, 3, 4, 5, 6, 7]
  }' | jq .

echo -e "\n==================================="
echo "Test 3: Estado con 3 opciones"
echo "==================================="
curl -X POST "${BASE_URL}/v1/graph/metrics" \
  -H "Content-Type: application/json" \
  -d '{
    "attempt_id": "test-uuid-123",
    "state": [1, 2, 0, 3, 4, 5, 6],
    "options": {
      "return_meta": true
    }
  }' | jq .

echo -e "\n==================================="
echo "Test 4: attempt_id no encontrado"
echo "==================================="
curl -X POST "${BASE_URL}/v1/graph/metrics" \
  -H "Content-Type: application/json" \
  -d '{
    "attempt_id": "non-existent-uuid",
    "state": [1, 2, 3, 0, 4, 5, 6]
  }' | jq .
