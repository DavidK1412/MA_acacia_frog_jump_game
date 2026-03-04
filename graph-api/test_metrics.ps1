# Script de prueba para endpoint /v1/graph/metrics (PowerShell)

$BASE_URL = "http://localhost:8080"

Write-Host "===================================" -ForegroundColor Cyan
Write-Host "Test 1: Branching N=3 inicial" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan

$body1 = @{
    attempt_id = "test-uuid-123"
    state = @(1, 2, 3, 0, 4, 5, 6)
    options = @{
        return_meta = $true
    }
} | ConvertTo-Json

Invoke-RestMethod -Uri "$BASE_URL/v1/graph/metrics" -Method Post -Body $body1 -ContentType "application/json" | ConvertTo-Json -Depth 10

Write-Host "`n===================================" -ForegroundColor Cyan
Write-Host "Test 2: Estado inválido (sin cero)" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan

$body2 = @{
    attempt_id = "test-uuid-123"
    state = @(1, 2, 3, 4, 5, 6, 7)
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$BASE_URL/v1/graph/metrics" -Method Post -Body $body2 -ContentType "application/json" | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error esperado: $_" -ForegroundColor Yellow
    $_.Exception.Response
}

Write-Host "`n===================================" -ForegroundColor Cyan
Write-Host "Test 3: Estado con 3 opciones" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan

$body3 = @{
    attempt_id = "test-uuid-123"
    state = @(1, 2, 0, 3, 4, 5, 6)
    options = @{
        return_meta = $true
    }
} | ConvertTo-Json

Invoke-RestMethod -Uri "$BASE_URL/v1/graph/metrics" -Method Post -Body $body3 -ContentType "application/json" | ConvertTo-Json -Depth 10

Write-Host "`n===================================" -ForegroundColor Cyan
Write-Host "Test 4: attempt_id no encontrado" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan

$body4 = @{
    attempt_id = "non-existent-uuid"
    state = @(1, 2, 3, 0, 4, 5, 6)
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$BASE_URL/v1/graph/metrics" -Method Post -Body $body4 -ContentType "application/json" | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error esperado (404): $_" -ForegroundColor Yellow
}
