package policy

type PolicyTable struct {
	Level   int
	Version string
	Dist    map[string]int
	Next    map[string][]int
	Degree  map[string]int
}

func NewPolicyTable(level int, version string) *PolicyTable {
	return &PolicyTable{
		Level:   level,
		Version: version,
		Dist:    make(map[string]int),
		Next:    make(map[string][]int),
		Degree:  make(map[string]int),
	}
}

func (pt *PolicyTable) SetState(stateKey string, dist int, nextState []int, degree int) {
	pt.Dist[stateKey] = dist
	pt.Next[stateKey] = nextState
	pt.Degree[stateKey] = degree
}

func (pt *PolicyTable) GetNextState(stateKey string) ([]int, int, bool) {
	nextState, exists := pt.Next[stateKey]
	if !exists {
		return nil, -1, false
	}
	dist := pt.Dist[stateKey]
	return nextState, dist, true
}

func (pt *PolicyTable) HasState(stateKey string) bool {
	_, exists := pt.Dist[stateKey]
	return exists
}
