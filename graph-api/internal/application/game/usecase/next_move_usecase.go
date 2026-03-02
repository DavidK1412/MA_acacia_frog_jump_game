package usecase

import (
	"graph-api/internal/domain/game/entity"
	"graph-api/internal/domain/game/policy"
	"graph-api/internal/domain/game/service"
	"time"
)

type NextMoveRequest struct {
	State           []int
	Goal            string
	ReturnNextState bool
	ReturnMeta      bool
}

type NextMoveResponse struct {
	Found     bool
	Level     int
	NextMove  *MoveDTO
	NextState []int
	Reason    string
	Meta      *MetaDTO
}

type MoveDTO struct {
	Type      string
	FrogId    int
	FromIndex int
	ToIndex   int
}

type MetaDTO struct {
	GoalState              []int
	PredictedRemainingCost int
	LegalMovesFromState    int
	Strategy               string
	TimeMs                 int64
	PolicyVersion          string
	LoadSource             string
}

type NextMoveUseCase struct {
	policyLookup   *service.PolicyLookupService
	movesGenerator *service.LegalMovesGenerator
	loadSource     string
}

func NewNextMoveUseCase(policyLoader *policy.PolicyLoader) *NextMoveUseCase {
	return &NextMoveUseCase{
		policyLookup:   service.NewPolicyLookupService(policyLoader),
		movesGenerator: service.NewLegalMovesGenerator(),
		loadSource:     policyLoader.LoadSource(),
	}
}

func (uc *NextMoveUseCase) Execute(req NextMoveRequest) (*NextMoveResponse, error) {
	startTime := time.Now()

	state, err := entity.NewState(req.State)
	if err != nil {
		return nil, err
	}

	move, nextState, dist, found := uc.policyLookup.FindNextMove(state)
	elapsed := time.Since(startTime).Milliseconds()

	response := &NextMoveResponse{
		Found: found,
		Level: state.Level(),
	}

	if found {
		response.NextMove = &MoveDTO{
			Type:      move.Type().String(),
			FrogId:    move.FrogId(),
			FromIndex: move.FromIndex(),
			ToIndex:   move.ToIndex(),
		}

		if req.ReturnNextState && nextState != nil {
			response.NextState = nextState.Cells()
		}
	} else {
		response.Reason = "POLICY_NOT_AVAILABLE"
	}

	if req.ReturnMeta {
		legalMoves := uc.movesGenerator.Generate(state)
		goalState := state.GoalState()

		response.Meta = &MetaDTO{
			GoalState:              goalState.Cells(),
			PredictedRemainingCost: dist,
			LegalMovesFromState:    len(legalMoves),
			Strategy:               "POLICY_TABLE",
			TimeMs:                 elapsed,
			PolicyVersion:          "level_" + string(rune('0'+state.Level())) + "_v1",
			LoadSource:             uc.loadSource,
		}
	}

	return response, nil
}
