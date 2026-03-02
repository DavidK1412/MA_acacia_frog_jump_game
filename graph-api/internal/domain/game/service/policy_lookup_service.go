package service

import (
	"fmt"
	"graph-api/internal/domain/game/entity"
	"graph-api/internal/domain/game/policy"
	"graph-api/internal/domain/game/valueobject"
)

type PolicyLookupService struct {
	loader         *policy.PolicyLoader
	movesGenerator *LegalMovesGenerator
}

func NewPolicyLookupService(loader *policy.PolicyLoader) *PolicyLookupService {
	return &PolicyLookupService{
		loader:         loader,
		movesGenerator: NewLegalMovesGenerator(),
	}
}

func (s *PolicyLookupService) FindNextMove(state *entity.State) (*entity.Move, *entity.State, int, bool) {
	level := state.Level()
	
	policyTable, exists := s.loader.GetPolicyTable(level)
	if !exists {
		return nil, nil, -1, false
	}
	
	stateKey := s.stateKey(state.Cells())
	nextState, dist, found := policyTable.GetNextState(stateKey)
	
	if !found {
		return nil, nil, -1, false
	}
	
	move := s.reconstructMove(state, nextState)
	if move == nil {
		return nil, nil, -1, false
	}
	
	nextStateEntity, _ := entity.NewState(nextState)
	return move, nextStateEntity, dist, true
}

func (s *PolicyLookupService) reconstructMove(fromState *entity.State, toStateCells []int) *entity.Move {
	fromCells := fromState.Cells()
	fromZeroIdx := fromState.ZeroIndex()
	
	toZeroIdx := -1
	for i, v := range toStateCells {
		if v == 0 {
			toZeroIdx = i
			break
		}
	}
	
	if toZeroIdx == -1 {
		return nil
	}
	
	frogId := fromCells[toZeroIdx]
	moveDistance := abs(toZeroIdx - fromZeroIdx)
	
	var moveType valueobject.MoveType
	if moveDistance == 1 {
		moveType = valueobject.MoveTypeStep
	} else if moveDistance == 2 {
		moveType = valueobject.MoveTypeJump
	} else {
		return nil
	}
	
	move := entity.NewMove(moveType, frogId, toZeroIdx, fromZeroIdx)
	
	resultState, err := fromState.ApplyMove(move)
	if err != nil {
		return nil
	}
	
	if !slicesEqual(resultState.Cells(), toStateCells) {
		return nil
	}
	
	return move
}

func (s *PolicyLookupService) stateKey(cells []int) string {
	key := ""
	for _, v := range cells {
		if v < 10 {
			key += string(rune('0' + v))
		} else {
			key += fmt.Sprintf("_%d_", v)
		}
	}
	return key
}

func abs(n int) int {
	if n < 0 {
		return -n
	}
	return n
}

func slicesEqual(a, b []int) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if a[i] != b[i] {
			return false
		}
	}
	return true
}
