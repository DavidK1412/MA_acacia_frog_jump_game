package usecase

import (
	"context"
	"graph-api/internal/domain/game/entity"
	gamerepository "graph-api/internal/domain/game/repository"
	"graph-api/internal/domain/game/service"
	"graph-api/internal/domain/game/valueobject"
	"time"
)

type MetricsUseCase struct {
	movementRepo       gamerepository.MovementRepository
	legalMovesGenerator *service.LegalMovesGenerator
}

type MetricsRequest struct {
	AttemptID  string
	State      []int
	ReturnMeta bool
}

type MetricsResponse struct {
	AttemptID string
	Level     int
	Branching BranchingResponse
	Cyclicity CyclicityResponse
	Meta      *MetaResponse
}

type BranchingResponse struct {
	Local     int `json:"local"`
	StepCount int `json:"step_count"`
	JumpCount int `json:"jump_count"`
}

type CyclicityResponse struct {
	TotalMoves     int     `json:"total_moves"`
	VisitedStates  int     `json:"visited_states"`
	UniqueStates   int     `json:"unique_states"`
	RepeatedStates int     `json:"repeated_states"`
	Cyclicity      float64 `json:"cyclicity"`
}

type MetaResponse struct {
	Source string `json:"source"`
	TimeMs int64  `json:"time_ms"`
}

func NewMetricsUseCase(movementRepo gamerepository.MovementRepository, legalMovesGenerator *service.LegalMovesGenerator) *MetricsUseCase {
	return &MetricsUseCase{
		movementRepo:       movementRepo,
		legalMovesGenerator: legalMovesGenerator,
	}
}

func (uc *MetricsUseCase) Execute(req MetricsRequest) (*MetricsResponse, error) {
	startTime := time.Now()

	state, err := entity.NewState(req.State)
	if err != nil {
		return nil, err
	}

	legalMoves := uc.legalMovesGenerator.Generate(state)

	stepCount := 0
	jumpCount := 0
	for _, move := range legalMoves {
		if move.Type() == valueobject.MoveTypeStep {
			stepCount++
		} else if move.Type() == valueobject.MoveTypeJump {
			jumpCount++
		}
	}

	branchingMetrics := entity.NewBranchingMetrics(len(legalMoves), stepCount, jumpCount)

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	cyclicityMetrics, err := uc.movementRepo.GetCyclicityMetrics(ctx, req.AttemptID)
	if err != nil {
		return nil, err
	}

	elapsed := time.Since(startTime)

	response := &MetricsResponse{
		AttemptID: req.AttemptID,
		Level:     state.Level(),
		Branching: BranchingResponse{
			Local:     branchingMetrics.Local(),
			StepCount: branchingMetrics.StepCount(),
			JumpCount: branchingMetrics.JumpCount(),
		},
		Cyclicity: CyclicityResponse{
			TotalMoves:     cyclicityMetrics.TotalMoves(),
			VisitedStates:  cyclicityMetrics.VisitedStates(),
			UniqueStates:   cyclicityMetrics.UniqueStates(),
			RepeatedStates: cyclicityMetrics.RepeatedStates(),
			Cyclicity:      cyclicityMetrics.Cyclicity(),
		},
	}

	if req.ReturnMeta {
		response.Meta = &MetaResponse{
			Source: "db",
			TimeMs: elapsed.Milliseconds(),
		}
	}

	return response, nil
}
