package repository

import (
	"context"
	"errors"
	"fmt"
	"graph-api/internal/domain/game/entity"

	"github.com/jackc/pgx/v5/pgxpool"
)

type PostgresMovementRepository struct {
	pool *pgxpool.Pool
}

func NewPostgresMovementRepository(pool *pgxpool.Pool) *PostgresMovementRepository {
	return &PostgresMovementRepository{
		pool: pool,
	}
}

func (r *PostgresMovementRepository) GetCyclicityMetrics(ctx context.Context, attemptID string) (*entity.CyclicityMetrics, error) {
	query := `
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
		WHERE attempt_id = $1
	`

	var totalMoves, visitedStates, uniqueStates, repeatedStates int
	var cyclicity float64

	err := r.pool.QueryRow(ctx, query, attemptID).Scan(
		&totalMoves,
		&visitedStates,
		&uniqueStates,
		&repeatedStates,
		&cyclicity,
	)

	if err != nil {
		return nil, fmt.Errorf("error querying cyclicity metrics: %w", err)
	}

	if visitedStates == 0 {
		return nil, errors.New("no movements found for attempt_id")
	}

	return entity.NewCyclicityMetrics(totalMoves, visitedStates, uniqueStates, repeatedStates, cyclicity), nil
}
