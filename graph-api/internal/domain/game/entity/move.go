package entity

import "graph-api/internal/domain/game/valueobject"

type Move struct {
	moveType  valueobject.MoveType
	frogId    int
	fromIndex int
	toIndex   int
}

func NewMove(moveType valueobject.MoveType, frogId, fromIndex, toIndex int) *Move {
	return &Move{
		moveType:  moveType,
		frogId:    frogId,
		fromIndex: fromIndex,
		toIndex:   toIndex,
	}
}

func (m *Move) Type() valueobject.MoveType {
	return m.moveType
}

func (m *Move) FrogId() int {
	return m.frogId
}

func (m *Move) FromIndex() int {
	return m.fromIndex
}

func (m *Move) ToIndex() int {
	return m.toIndex
}
