# Graph Service Spec — POST /v1/graph/metrics (branching + cyclicity) — PostgreSQL

This document defines the contract for **/v1/graph/metrics** to compute:
- **Branching** from the current state (legal moves).
- **Cyclicity** ("buclicidad") from the real attempt trajectory stored in **PostgreSQL** for an `attempt_id`.

**Cyclicity definition (simple, weight=1 per move):**
> **cyclicity = repeated_states / total_moves**

---

## 1) Endpoint

**POST** `/v1/graph/metrics`

---

## 2) Request (snake_case)

### Recommended request (minimum useful)
```json
{
  "attempt_id": "8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999",
  "state": [1, 2, 3, 0, 4, 5, 6],
  "options": {
    "return_meta": true
  }
}
```

### Fields
- `attempt_id` (**required**): Attempt UUID. Used to fetch the attempt history from DB.
- `state` (**required**): Current board state (integer list) to compute local branching.
- `options.return_meta` (optional): Include debug/performance metadata.

---

## 3) Mandatory state validation (server-side)

Return `400 invalid_state` if:

1) `state` does not contain `0` exactly once  
2) duplicated values exist  
3) `max_id = max(state)` is not even (`max_id % 2 != 0`) since `max_id = 2N`  
4) `len(state) != max_id + 1` (equals `2N + 1`)  
5) the set is not exactly `{0,1,2,...,2N}`

### 400 invalid_state
```json
{
  "error": "invalid_state",
  "message": "state must contain exactly once each value from 0..2N and exactly one zero."
}
```

---

## 4) Metric computations (normative)

### 4.1 Level detection
- `n = max(state) / 2`
- `level = n`

### 4.2 Branching
**Exact local branching:**
- `branching.local = count(legal_moves(state))`

Legal moves must be computed using official STEP/JUMP rules and team directions.

**Recommended counters:**
- `branching.step_count`
- `branching.jump_count`

Known example (N=3 initial state):
- `state = [1,2,3,0,4,5,6]` → `branching.local = 2`, `step_count = 2`, `jump_count = 0`.

### 4.3 Cyclicity — from PostgreSQL by `attempt_id`

#### Data requirements
The `movements` table must include a `state_hash` column per row.

**Required convention (initial state stored as an extra step):**
- A row with `step = 0` represents the initial state (INIT).
- Each real move increments `step` (1..M).

Then:
- `visited_states = count(*)` (includes `step=0`)
- `total_moves = count(*) - 1`
- `unique_states = count(distinct state_hash)`
- `repeated_states = visited_states - unique_states`
- `cyclicity = repeated_states / total_moves` (if `total_moves > 0`, else 0)

#### Final definition
\[
cyclicity = \frac{repeated\_states}{total\_moves}
\]

Edge case:
- If `total_moves = 0` → `cyclicity = 0`

---

## 5) Response

### 200 OK — example
```json
{
  "attempt_id": "8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999",
  "level": 3,
  "branching": {
    "local": 2,
    "step_count": 2,
    "jump_count": 0
  },
  "cyclicity": {
    "total_moves": 20,
    "visited_states": 21,
    "unique_states": 15,
    "repeated_states": 6,
    "cyclicity": 0.3
  },
  "meta": {
    "source": "db",
    "time_ms": 6
  }
}
```

### 404 attempt_not_found (recommended)
If `attempt_id` does not exist or has no rows in `movements`:
```json
{
  "error": "attempt_not_found",
  "message": "no movements found for attempt_id."
}
```

---

## 6) PostgreSQL — Reference SQL (cyclicity)

Requires `step=0` (INIT) and non-null `state_hash`.

```sql
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
WHERE attempt_id = $1;
```

---

## 7) PostgreSQL — Suggested schema changes & indexes

### 7.1 Add `state_hash`
```sql
ALTER TABLE movements
  ADD COLUMN state_hash VARCHAR(64) NOT NULL;
```

### 7.2 Indexes
```sql
CREATE INDEX IF NOT EXISTS idx_movements_attempt_step
  ON movements(attempt_id, step);

CREATE INDEX IF NOT EXISTS idx_movements_attempt_state_hash
  ON movements(attempt_id, state_hash);
```

---

## 8) PostgreSQL driver & connection (Go)

Recommendation:
- Driver: `pgx` (fast, modern) or `database/sql` + `pgx` stdlib.
- Pool: `pgxpool`.

Suggested env vars:
- `DATABASE_URL` (e.g. `postgres://user:pass@host:5432/dbname?sslmode=require`)

---

## 9) Test matrix (minimum required)

| ID | Case | Input | Expected |
|---:|---|---|---|
| M01 | branching N=3 initial | `state=[1,2,3,0,4,5,6]` | `branching.local=2`, `step_count=2`, `jump_count=0` |
| M02 | branching N=4 initial | `state=[1,2,3,4,0,5,6,7,8]` | `branching.local=2` (typical) |
| M03 | branching with 3 options | `state=[1,2,0,3,4,5,6]` | `branching.local=3` |
| M04 | invalid state (missing 0) | `state=[1,2,3,4,5,6,7]` | `400 invalid_state` |
| M05 | cyclicity with INIT | attempt has steps `0..M` | `total_moves=count-1`, cyclicity correct |
| M06 | cyclicity M=0 | attempt only has `step=0` | `total_moves=0`, `cyclicity=0` |
| M07 | repeated states | repeated hashes present | `repeated_states = visited_states - unique_states` |
| M08 | attempt not found | attempt_id has no rows | `404 attempt_not_found` |
| M09 | performance | large attempt | query < 50ms (same region) |
| M10 | consistency | repeated calls | stable metrics for same attempt_id/state |

---

## 10) Definition of Done

- [ ] Strict state validation
- [ ] `branching.local`, `step_count`, `jump_count` computed with official rules
- [ ] Cyclicity computed from PostgreSQL including `step=0`
- [ ] 400 and 404 responses match this spec
- [ ] Indexes created
- [ ] Tests M01–M10
