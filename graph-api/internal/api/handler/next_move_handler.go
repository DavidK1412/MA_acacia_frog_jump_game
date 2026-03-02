package handler

import (
	"encoding/json"
	"graph-api/internal/application/game/usecase"
	"net/http"
)

type NextMoveHandler struct {
	useCase *usecase.NextMoveUseCase
}

func NewNextMoveHandler(useCase *usecase.NextMoveUseCase) *NextMoveHandler {
	return &NextMoveHandler{
		useCase: useCase,
	}
}

type NextMoveRequestDTO struct {
	State   []int               `json:"state"`
	Goal    string              `json:"goal,omitempty"`
	Limits  *LimitsDTO          `json:"limits,omitempty"`
	Options *OptionsDTO         `json:"options,omitempty"`
}

type LimitsDTO struct {
	TimeoutMs int `json:"timeoutMs,omitempty"`
	MaxNodes  int `json:"maxNodes,omitempty"`
	MaxDepth  int `json:"maxDepth,omitempty"`
}

type OptionsDTO struct {
	ReturnNextState bool `json:"returnNextState,omitempty"`
	ReturnMeta      bool `json:"returnMeta,omitempty"`
}

type NextMoveResponseDTO struct {
	Found     bool               `json:"found"`
	Level     int                `json:"level"`
	NextMove  *MoveResponseDTO   `json:"nextMove,omitempty"`
	NextState []int              `json:"nextState,omitempty"`
	Reason    string             `json:"reason,omitempty"`
	Meta      *MetaResponseDTO   `json:"meta,omitempty"`
}

type MoveResponseDTO struct {
	Type      string `json:"type"`
	FrogId    int    `json:"frogId"`
	FromIndex int    `json:"fromIndex"`
	ToIndex   int    `json:"toIndex"`
}

type MetaResponseDTO struct {
	GoalState              []int  `json:"goalState,omitempty"`
	PredictedRemainingCost int    `json:"predictedRemainingCost,omitempty"`
	LegalMovesFromState    int    `json:"legalMovesFromState,omitempty"`
	Strategy               string `json:"strategy"`
	TimeMs                 int64  `json:"timeMs"`
	PolicyVersion          string `json:"policyVersion,omitempty"`
	LoadSource             string `json:"loadSource,omitempty"`
}

type ErrorResponseDTO struct {
	Error   string                 `json:"error"`
	Message string                 `json:"message"`
	Details map[string]interface{} `json:"details,omitempty"`
}

func (h *NextMoveHandler) Handle(w http.ResponseWriter, r *http.Request) {
	var reqDTO NextMoveRequestDTO
	if err := json.NewDecoder(r.Body).Decode(&reqDTO); err != nil {
		respondError(w, http.StatusBadRequest, "INVALID_REQUEST", "Invalid JSON body", nil)
		return
	}

	if reqDTO.State == nil || len(reqDTO.State) == 0 {
		respondError(w, http.StatusBadRequest, "INVALID_STATE", "State is required", nil)
		return
	}

	req := usecase.NextMoveRequest{
		State: reqDTO.State,
		Goal:  reqDTO.Goal,
	}

	if reqDTO.Options != nil {
		req.ReturnNextState = reqDTO.Options.ReturnNextState
		req.ReturnMeta = reqDTO.Options.ReturnMeta
	}

	response, err := h.useCase.Execute(req)
	if err != nil {
		respondError(w, http.StatusBadRequest, "INVALID_STATE", err.Error(), nil)
		return
	}

	respDTO := &NextMoveResponseDTO{
		Found:  response.Found,
		Level:  response.Level,
		Reason: response.Reason,
	}

	if response.NextMove != nil {
		respDTO.NextMove = &MoveResponseDTO{
			Type:      response.NextMove.Type,
			FrogId:    response.NextMove.FrogId,
			FromIndex: response.NextMove.FromIndex,
			ToIndex:   response.NextMove.ToIndex,
		}
	}

	if response.NextState != nil {
		respDTO.NextState = response.NextState
	}

	if response.Meta != nil {
		respDTO.Meta = &MetaResponseDTO{
			GoalState:              response.Meta.GoalState,
			PredictedRemainingCost: response.Meta.PredictedRemainingCost,
			LegalMovesFromState:    response.Meta.LegalMovesFromState,
			Strategy:               response.Meta.Strategy,
			TimeMs:                 response.Meta.TimeMs,
			PolicyVersion:          response.Meta.PolicyVersion,
			LoadSource:             response.Meta.LoadSource,
		}
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(respDTO)
}

func respondError(w http.ResponseWriter, status int, errorCode, message string, details map[string]interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(ErrorResponseDTO{
		Error:   errorCode,
		Message: message,
		Details: details,
	})
}
