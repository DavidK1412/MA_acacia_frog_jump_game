package policygen

import (
	"encoding/json"
	"fmt"
	"graph-api/internal/domain/game/entity"
	"graph-api/internal/domain/game/policy"
	"graph-api/internal/domain/game/service"
	"os"
)

type PolicyGenerator struct {
	movesGenerator *service.LegalMovesGenerator
}

func NewPolicyGenerator() *PolicyGenerator {
	return &PolicyGenerator{
		movesGenerator: service.NewLegalMovesGenerator(),
	}
}

func (g *PolicyGenerator) Generate(level int) (*policy.PolicyTable, error) {
	goalCells := createGoalState(level)
	
	policyTable := policy.NewPolicyTable(level, fmt.Sprintf("level_%d_v1", level))
	
	type queueNode struct {
		state []int
		dist  int
	}
	
	queue := []queueNode{{state: goalCells, dist: 0}}
	visited := make(map[string]bool)
	goalKey := stateKey(goalCells)
	visited[goalKey] = true
	
	policyTable.SetState(goalKey, 0, goalCells, 0)
	
	for len(queue) > 0 {
		current := queue[0]
		queue = queue[1:]
		
		currentState, err := entity.NewState(current.state)
		if err != nil {
			continue
		}
		
		predecessors := g.findPredecessors(currentState)
		
		for _, predCells := range predecessors {
			predKey := stateKey(predCells)
			
			if visited[predKey] {
				continue
			}
			visited[predKey] = true
			
			policyTable.SetState(predKey, current.dist+1, current.state, 0)
			
			queue = append(queue, queueNode{
				state: predCells,
				dist:  current.dist + 1,
			})
		}
	}
	
	for stateKey := range policyTable.Dist {
		stateCells := keyToState(stateKey)
		state, err := entity.NewState(stateCells)
		if err != nil {
			continue
		}
		legalMoves := g.movesGenerator.Generate(state)
		policyTable.Degree[stateKey] = len(legalMoves)
	}
	
	return policyTable, nil
}

func (g *PolicyGenerator) findPredecessors(state *entity.State) [][]int {
	var predecessors [][]int
	cells := state.Cells()
	zeroIdx := state.ZeroIndex()
	
	for i := 0; i < len(cells); i++ {
		if i == zeroIdx {
			continue
		}
		
		predCells := make([]int, len(cells))
		copy(predCells, cells)
		predCells[zeroIdx], predCells[i] = predCells[i], predCells[zeroIdx]
		
		predState, err := entity.NewState(predCells)
		if err != nil {
			continue
		}
		
		moves := g.movesGenerator.Generate(predState)
		for _, move := range moves {
			resultState, err := predState.ApplyMove(move)
			if err != nil {
				continue
			}
			
			if slicesEqual(resultState.Cells(), cells) {
				predecessors = append(predecessors, predCells)
				break
			}
		}
	}
	
	return predecessors
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

func keyToState(key string) []int {
	var cells []int
	i := 0
	for i < len(key) {
		if key[i] == '_' {
			i++
			numStr := ""
			for i < len(key) && key[i] != '_' {
				numStr += string(key[i])
				i++
			}
			i++
			var num int
			fmt.Sscanf(numStr, "%d", &num)
			cells = append(cells, num)
		} else {
			cells = append(cells, int(key[i]-'0'))
			i++
		}
	}
	return cells
}

func (g *PolicyGenerator) SaveToFile(policyTable *policy.PolicyTable, filepath string) error {
	data, err := json.MarshalIndent(policyTable, "", "  ")
	if err != nil {
		return err
	}
	
	return os.WriteFile(filepath, data, 0644)
}

func createGoalState(level int) []int {
	cells := make([]int, 2*level+1)
	
	for i := 0; i < level; i++ {
		cells[i] = level + 1 + i
	}
	
	cells[level] = 0
	
	for i := 0; i < level; i++ {
		cells[level+1+i] = i + 1
	}
	
	return cells
}

func stateKey(cells []int) string {
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
