package repository

import (
	"context"
	"graph-api/internal/domain/health/entity"
)

type InMemoryHealthRepository struct{}

func NewInMemoryHealthRepository() *InMemoryHealthRepository {
	return &InMemoryHealthRepository{}
}

func (r *InMemoryHealthRepository) GetHealth(ctx context.Context) (*entity.Health, error) {
	return entity.NewHealth("healthy", "pong"), nil
}
