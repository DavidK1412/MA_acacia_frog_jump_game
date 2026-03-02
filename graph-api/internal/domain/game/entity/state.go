package entity

import (
	"errors"
	"fmt"
)

type State struct {
	cells []int
	level int
}

func NewState(cells []int) (*State, error) {
	if err := validateState(cells); err != nil {
		return nil, err
	}

	maxID := maxValue(cells)
	level := maxID / 2

	return &State{
		cells: copyCells(cells),
		level: level,
	}, nil
}

func (s *State) Cells() []int {
	return copyCells(s.cells)
}

func (s *State) Level() int {
	return s.level
}

func (s *State) ZeroIndex() int {
	for i, v := range s.cells {
		if v == 0 {
			return i
		}
	}
	return -1
}

func (s *State) ApplyMove(move *Move) (*State, error) {
	newCells := copyCells(s.cells)
	
	if move.ToIndex() != s.ZeroIndex() {
		return nil, errors.New("move toIndex must be current zero position")
	}

	newCells[move.FromIndex()], newCells[move.ToIndex()] = newCells[move.ToIndex()], newCells[move.FromIndex()]
	
	return NewState(newCells)
}

func (s *State) IsGoal() bool {
	expected := s.GoalState()
	return slicesEqual(s.cells, expected.cells)
}

func (s *State) GoalState() *State {
	n := s.level
	cells := make([]int, 2*n+1)
	
	for i := 0; i < n; i++ {
		cells[i] = n + 1 + i
	}
	
	cells[n] = 0
	
	for i := 0; i < n; i++ {
		cells[n+1+i] = i + 1
	}
	
	state, _ := NewState(cells)
	return state
}

func validateState(cells []int) error {
	if len(cells) == 0 {
		return errors.New("state cannot be empty")
	}

	maxID := maxValue(cells)
	
	if maxID%2 != 0 {
		return fmt.Errorf("max value must be even, got %d", maxID)
	}

	expectedLen := maxID + 1
	if len(cells) != expectedLen {
		return fmt.Errorf("state length must be %d, got %d", expectedLen, len(cells))
	}

	seen := make(map[int]bool)
	zeroCount := 0

	for _, v := range cells {
		if v < 0 || v > maxID {
			return fmt.Errorf("value %d out of range [0, %d]", v, maxID)
		}
		
		if v == 0 {
			zeroCount++
		}
		
		if seen[v] {
			return fmt.Errorf("duplicate value %d", v)
		}
		seen[v] = true
	}

	if zeroCount != 1 {
		return fmt.Errorf("state must contain exactly one zero, got %d", zeroCount)
	}

	for i := 0; i <= maxID; i++ {
		if !seen[i] {
			return fmt.Errorf("missing value %d", i)
		}
	}

	return nil
}

func maxValue(cells []int) int {
	max := 0
	for _, v := range cells {
		if v > max {
			max = v
		}
	}
	return max
}

func copyCells(cells []int) []int {
	copied := make([]int, len(cells))
	copy(copied, cells)
	return copied
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
