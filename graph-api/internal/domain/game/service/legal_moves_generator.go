package service

import (
	"graph-api/internal/domain/game/entity"
	"graph-api/internal/domain/game/valueobject"
)

type LegalMovesGenerator struct{}

func NewLegalMovesGenerator() *LegalMovesGenerator {
	return &LegalMovesGenerator{}
}

func (g *LegalMovesGenerator) Generate(state *entity.State) []*entity.Move {
	cells := state.Cells()
	zeroIdx := state.ZeroIndex()
	n := state.Level()
	
	var moves []*entity.Move

	if zeroIdx > 0 {
		frogId := cells[zeroIdx-1]
		if canMoveRight(frogId, n) {
			move := entity.NewMove(valueobject.MoveTypeStep, frogId, zeroIdx-1, zeroIdx)
			moves = append(moves, move)
		}
	}

	if zeroIdx < len(cells)-1 {
		frogId := cells[zeroIdx+1]
		if canMoveLeft(frogId, n) {
			move := entity.NewMove(valueobject.MoveTypeStep, frogId, zeroIdx+1, zeroIdx)
			moves = append(moves, move)
		}
	}

	if zeroIdx > 1 {
		frogId := cells[zeroIdx-2]
		middleFrog := cells[zeroIdx-1]
		if canMoveRight(frogId, n) && isOpposite(frogId, middleFrog, n) {
			move := entity.NewMove(valueobject.MoveTypeJump, frogId, zeroIdx-2, zeroIdx)
			moves = append(moves, move)
		}
	}

	if zeroIdx < len(cells)-2 {
		frogId := cells[zeroIdx+2]
		middleFrog := cells[zeroIdx+1]
		if canMoveLeft(frogId, n) && isOpposite(frogId, middleFrog, n) {
			move := entity.NewMove(valueobject.MoveTypeJump, frogId, zeroIdx+2, zeroIdx)
			moves = append(moves, move)
		}
	}

	return moves
}

func canMoveRight(frogId, n int) bool {
	return frogId > 0 && frogId <= n
}

func canMoveLeft(frogId, n int) bool {
	return frogId > n
}

func isOpposite(frog1, frog2, n int) bool {
	if frog1 == 0 || frog2 == 0 {
		return false
	}
	
	left1 := frog1 <= n
	left2 := frog2 <= n
	
	return left1 != left2
}
