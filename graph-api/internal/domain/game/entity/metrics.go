package entity

type MetricsResult struct {
	attemptID string
	level     int
	branching *BranchingMetrics
	cyclicity *CyclicityMetrics
}

type BranchingMetrics struct {
	local     int
	stepCount int
	jumpCount int
}

type CyclicityMetrics struct {
	totalMoves     int
	visitedStates  int
	uniqueStates   int
	repeatedStates int
	cyclicity      float64
}

func NewMetricsResult(attemptID string, level int, branching *BranchingMetrics, cyclicity *CyclicityMetrics) *MetricsResult {
	return &MetricsResult{
		attemptID: attemptID,
		level:     level,
		branching: branching,
		cyclicity: cyclicity,
	}
}

func NewBranchingMetrics(local, stepCount, jumpCount int) *BranchingMetrics {
	return &BranchingMetrics{
		local:     local,
		stepCount: stepCount,
		jumpCount: jumpCount,
	}
}

func NewCyclicityMetrics(totalMoves, visitedStates, uniqueStates, repeatedStates int, cyclicity float64) *CyclicityMetrics {
	return &CyclicityMetrics{
		totalMoves:     totalMoves,
		visitedStates:  visitedStates,
		uniqueStates:   uniqueStates,
		repeatedStates: repeatedStates,
		cyclicity:      cyclicity,
	}
}

func (m *MetricsResult) AttemptID() string {
	return m.attemptID
}

func (m *MetricsResult) Level() int {
	return m.level
}

func (m *MetricsResult) Branching() *BranchingMetrics {
	return m.branching
}

func (m *MetricsResult) Cyclicity() *CyclicityMetrics {
	return m.cyclicity
}

func (b *BranchingMetrics) Local() int {
	return b.local
}

func (b *BranchingMetrics) StepCount() int {
	return b.stepCount
}

func (b *BranchingMetrics) JumpCount() int {
	return b.jumpCount
}

func (c *CyclicityMetrics) TotalMoves() int {
	return c.totalMoves
}

func (c *CyclicityMetrics) VisitedStates() int {
	return c.visitedStates
}

func (c *CyclicityMetrics) UniqueStates() int {
	return c.uniqueStates
}

func (c *CyclicityMetrics) RepeatedStates() int {
	return c.repeatedStates
}

func (c *CyclicityMetrics) Cyclicity() float64 {
	return c.cyclicity
}
