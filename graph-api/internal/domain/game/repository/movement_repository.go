package repository

import (
	"context"
	"graph-api/internal/domain/game/entity"
)

type MovementRepository interface {
	GetCyclicityMetrics(ctx context.Context, attemptID string) (*entity.CyclicityMetrics, error)
}
