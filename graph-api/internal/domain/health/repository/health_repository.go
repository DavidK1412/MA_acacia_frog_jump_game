package repository

import (
	"context"
	"graph-api/internal/domain/health/entity"
)

type HealthRepository interface {
	GetHealth(ctx context.Context) (*entity.Health, error)
}
