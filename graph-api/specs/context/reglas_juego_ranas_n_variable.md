# Reglas del Juego — Ranas (N variable) + Ejemplos (válidos e inválidos)

Este documento define las reglas del juego de las ranas para niveles con **N ranas por equipo** y provee ejemplos para **N = 3, 4, 5**.

---

## 1) Representación del estado

Un estado es una lista de enteros con longitud **2N + 1**.

- `0` representa el hueco.
- Equipo izquierdo: IDs `1..N`
- Equipo derecho: IDs `N+1..2N`

Ejemplos de estados iniciales por nivel:

### Nivel N=3
`[1, 2, 3, 0, 4, 5, 6]`

### Nivel N=4
`[1, 2, 3, 4, 0, 5, 6, 7, 8]`

### Nivel N=5
`[1, 2, 3, 4, 5, 0, 6, 7, 8, 9, 10]`

---

## 2) Estado objetivo (goal por defecto)

El objetivo es intercambiar los equipos dejando el hueco al centro:

- goal(N) = `[N+1..2N, 0, 1..N]`

Ejemplos:

- N=3 → `[4, 5, 6, 0, 1, 2, 3]`
- N=4 → `[5, 6, 7, 8, 0, 1, 2, 3, 4]`
- N=5 → `[6, 7, 8, 9, 10, 0, 1, 2, 3, 4, 5]`

---

## 3) Reglas de movimiento (normativas)

### Direcciones permitidas
- Ranas del equipo izquierdo (`1..N`) **solo se mueven hacia la derecha**.
- Ranas del equipo derecho (`N+1..2N`) **solo se mueven hacia la izquierda**.

### Tipos de movimiento (costo 1)

#### A) STEP (paso simple)
Mover una rana 1 casilla hacia el hueco.

- Si `frogId <= N` (izquierda): debe mover **+1** (derecha).
- Si `frogId > N` (derecha): debe mover **-1** (izquierda).
- El destino debe ser exactamente la posición del `0`.

#### B) JUMP (salto)
Mover una rana 2 casillas hacia el hueco, saltando una rana del equipo contrario.

- Si `frogId <= N` (izquierda): debe mover **+2** y la casilla intermedia debe ser una rana `> N`.
- Si `frogId > N` (derecha): debe mover **-2** y la casilla intermedia debe ser una rana `<= N`.
- El destino debe ser exactamente la posición del `0`.

---

## 4) Ejemplos con N=3

### Ejemplo base
Estado:
`[1, 2, 3, 0, 4, 5, 6]`
- índice del `0` = 3

### Movimiento válido (STEP)
Mover `3` (en índice 2) al hueco (índice 3):
- `3` es izquierda (<=3), puede mover +1.
- Resultado: `[1, 2, 0, 3, 4, 5, 6]`

Representación:
- type: STEP
- frogId: 3
- fromIndex: 2
- toIndex: 3

### Movimiento válido (JUMP)
Desde:
`[1, 2, 0, 3, 4, 5, 6]`
- índice del `0` = 2

Mover `4` (derecha) desde índice 4 a índice 2 saltando a `3`:
- `4` es derecha (>3), puede mover -2
- intermedio (índice 3) = `3` (izquierda) ✅
- Resultado: `[1, 2, 4, 3, 0, 5, 6]`

Representación:
- type: JUMP
- frogId: 4
- fromIndex: 4
- toIndex: 2

### Movimientos inválidos (ejemplos)
1) **Dirección prohibida**
Intentar mover `2` (izquierda) hacia la izquierda: inválido.

2) **Destino no es el hueco**
Si el destino no coincide con la posición del `0`, inválido.

3) **JUMP sin enemigo en medio**
Si una rana izquierda salta +2 pero en medio hay una rana izquierda (<=N), inválido.

---

## 5) Ejemplos con N=4

Estado:
`[1, 2, 3, 4, 0, 5, 6, 7, 8]`
- `0` en índice 4

### Válido (STEP)
Mover `4` (izquierda) de índice 3 a índice 4:
Resultado:
`[1, 2, 3, 0, 4, 5, 6, 7, 8]`

### Inválido (JUMP sin enemigo)
En ese mismo estado, intentar que `3` salte a índice 4 desde índice 2:
- salto sería +2 (2→4), intermedio índice 3 = `4` (izquierda) ❌
Inválido.

---

## 6) Ejemplos con N=5

Estado:
`[1, 2, 3, 4, 5, 0, 6, 7, 8, 9, 10]`
- `0` en índice 5

### Válido (STEP)
Mover `5` (izquierda) de índice 4 a índice 5:
Resultado:
`[1, 2, 3, 4, 0, 5, 6, 7, 8, 9, 10]`

### Válido (STEP)
Mover `6` (derecha) de índice 6 a índice 5:
Resultado:
`[1, 2, 3, 4, 5, 6, 0, 7, 8, 9, 10]`

### Inválido (derecha hacia la derecha)
Intentar mover `6` de índice 6 a índice 7:
- `6` pertenece al equipo derecho (>5) y solo puede moverse a la izquierda ❌

---

## 7) Validación rápida del estado (sanity)

Un estado es válido si:
- contiene exactamente una vez cada valor de `0..2N`
- tiene longitud `2N + 1`
- `max(state) = 2N` (par)

---

## 8) Nota práctica para el API

Un `nextMove` correcto debe cumplir:
- `toIndex` = índice del `0`
- respeta STEP/JUMP
- respeta direcciones
- si es JUMP, el intermedio debe ser enemigo

Esto garantiza que el servicio nunca devuelva un “best-next” ilegal.
