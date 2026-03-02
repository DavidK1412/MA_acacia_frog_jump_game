package algorithm

import (
	"graph-api/internal/domain/game/entity"
	"graph-api/internal/domain/game/service"
	"time"
)

type SearchLimits struct {
	TimeoutMs int
	MaxNodes  int
	MaxDepth  int
}

type SearchResult struct {
	Found                  bool
	FirstMove              *entity.Move
	NextState              *entity.State
	PredictedRemainingCost int
	ExpandedNodes          int
	GeneratedEdges         int
	LimitHit               string
}

type BFSSolver struct {
	movesGenerator *service.LegalMovesGenerator
}

func NewBFSSolver() *BFSSolver {
	return &BFSSolver{
		movesGenerator: service.NewLegalMovesGenerator(),
	}
}

func (s *BFSSolver) FindNextMove(
	initialState *entity.State,
	limits SearchLimits,
) *SearchResult {
	startTime := time.Now()
	
	if initialState.IsGoal() {
		return &SearchResult{
			Found:          false,
			ExpandedNodes:  0,
			GeneratedEdges: 0,
		}
	}

	type queueNode struct {
		state     *entity.State
		firstMove *entity.Move
		depth     int
	}

	queue := []*queueNode{{state: initialState, firstMove: nil, depth: 0}}
	visited := make(map[string]bool)
	visited[stateKey(initialState)] = true

	expandedNodes := 0
	generatedEdges := 0

	for len(queue) > 0 {
		if limits.TimeoutMs > 0 {
			elapsed := time.Since(startTime).Milliseconds()
			if elapsed >= int64(limits.TimeoutMs) {
				return &SearchResult{
					Found:          false,
					ExpandedNodes:  expandedNodes,
					GeneratedEdges: generatedEdges,
					LimitHit:       "timeoutMs",
				}
			}
		}

		if limits.MaxNodes > 0 && expandedNodes >= limits.MaxNodes {
			return &SearchResult{
				Found:          false,
				ExpandedNodes:  expandedNodes,
				GeneratedEdges: generatedEdges,
				LimitHit:       "maxNodes",
			}
		}

		current := queue[0]
		queue = queue[1:]
		expandedNodes++

		if limits.MaxDepth > 0 && current.depth >= limits.MaxDepth {
			continue
		}

		legalMoves := s.movesGenerator.Generate(current.state)

		for _, move := range legalMoves {
			generatedEdges++

			nextState, err := current.state.ApplyMove(move)
			if err != nil {
				continue
			}

			key := stateKey(nextState)
			if visited[key] {
				continue
			}
			visited[key] = true

			firstMove := move
			if current.firstMove != nil {
				firstMove = current.firstMove
			}

			if nextState.IsGoal() {
				return &SearchResult{
					Found:                  true,
					FirstMove:              firstMove,
					NextState:              nextState,
					PredictedRemainingCost: current.depth + 1,
					ExpandedNodes:          expandedNodes,
					GeneratedEdges:         generatedEdges,
				}
			}

			queue = append(queue, &queueNode{
				state:     nextState,
				firstMove: firstMove,
				depth:     current.depth + 1,
			})
		}
	}

	return &SearchResult{
		Found:          false,
		ExpandedNodes:  expandedNodes,
		GeneratedEdges: generatedEdges,
	}
}

func stateKey(state *entity.State) string {
	cells := state.Cells()
	key := ""
	for _, v := range cells {
		key += string(rune('0' + v))
	}
	return key
}
