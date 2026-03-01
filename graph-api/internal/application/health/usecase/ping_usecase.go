package usecase

import (
	"context"
	"graph-api/internal/domain/health/entity"
	"graph-api/internal/domain/health/repository"
)

type PingUseCase struct {
	healthRepo repository.HealthRepository
}

func NewPingUseCase(healthRepo repository.HealthRepository) *PingUseCase {
	return &PingUseCase{
		healthRepo: healthRepo,
	}
}

func (uc *PingUseCase) Execute(ctx context.Context) (*entity.Health, error) {
	return uc.healthRepo.GetHealth(ctx)
}
