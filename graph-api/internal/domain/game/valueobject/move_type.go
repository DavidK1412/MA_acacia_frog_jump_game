package valueobject

type MoveType string

const (
	MoveTypeStep MoveType = "STEP"
	MoveTypeJump MoveType = "JUMP"
)

func (mt MoveType) String() string {
	return string(mt)
}

func (mt MoveType) IsValid() bool {
	return mt == MoveTypeStep || mt == MoveTypeJump
}
