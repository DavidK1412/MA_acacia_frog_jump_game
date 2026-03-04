package handler

import (
	"encoding/json"
	"graph-api/internal/application/game/usecase"
	"net/http"
)

type MetricsHandler struct {
	useCase *usecase.MetricsUseCase
}

func NewMetricsHandler(useCase *usecase.MetricsUseCase) *MetricsHandler {
	return &MetricsHandler{
		useCase: useCase,
	}
}

type MetricsRequestDTO struct {
	AttemptID string               `json:"attempt_id"`
	State     []int                `json:"state"`
	Options   *MetricsOptionsDTO   `json:"options,omitempty"`
}

type MetricsOptionsDTO struct {
	ReturnMeta bool `json:"return_meta,omitempty"`
}

type MetricsResponseDTO struct {
	AttemptID string                   `json:"attempt_id"`
	Level     int                      `json:"level"`
	Branching BranchingMetricsDTO      `json:"branching"`
	Cyclicity CyclicityMetricsDTO      `json:"cyclicity"`
	Meta      *MetricsMetaResponseDTO  `json:"meta,omitempty"`
}

type BranchingMetricsDTO struct {
	Local     int `json:"local"`
	StepCount int `json:"step_count"`
	JumpCount int `json:"jump_count"`
}

type CyclicityMetricsDTO struct {
	TotalMoves     int     `json:"total_moves"`
	VisitedStates  int     `json:"visited_states"`
	UniqueStates   int     `json:"unique_states"`
	RepeatedStates int     `json:"repeated_states"`
	Cyclicity      float64 `json:"cyclicity"`
}

type MetricsMetaResponseDTO struct {
	Source string `json:"source"`
	TimeMs int64  `json:"time_ms"`
}

func (h *MetricsHandler) Handle(w http.ResponseWriter, r *http.Request) {
	var reqDTO MetricsRequestDTO
	if err := json.NewDecoder(r.Body).Decode(&reqDTO); err != nil {
		respondError(w, http.StatusBadRequest, "invalid_request", "Invalid JSON body", nil)
		return
	}

	if reqDTO.AttemptID == "" {
		respondError(w, http.StatusBadRequest, "invalid_request", "attempt_id is required", nil)
		return
	}

	if reqDTO.State == nil || len(reqDTO.State) == 0 {
		respondError(w, http.StatusBadRequest, "invalid_state", "state is required", nil)
		return
	}

	req := usecase.MetricsRequest{
		AttemptID:  reqDTO.AttemptID,
		State:      reqDTO.State,
		ReturnMeta: false,
	}

	if reqDTO.Options != nil {
		req.ReturnMeta = reqDTO.Options.ReturnMeta
	}

	response, err := h.useCase.Execute(req)
	if err != nil {
		if err.Error() == "no movements found for attempt_id" {
			respondError(w, http.StatusNotFound, "attempt_not_found", "no movements found for attempt_id.", nil)
			return
		}
		respondError(w, http.StatusBadRequest, "invalid_state", err.Error(), nil)
		return
	}

	respDTO := &MetricsResponseDTO{
		AttemptID: response.AttemptID,
		Level:     response.Level,
		Branching: BranchingMetricsDTO{
			Local:     response.Branching.Local,
			StepCount: response.Branching.StepCount,
			JumpCount: response.Branching.JumpCount,
		},
		Cyclicity: CyclicityMetricsDTO{
			TotalMoves:     response.Cyclicity.TotalMoves,
			VisitedStates:  response.Cyclicity.VisitedStates,
			UniqueStates:   response.Cyclicity.UniqueStates,
			RepeatedStates: response.Cyclicity.RepeatedStates,
			Cyclicity:      response.Cyclicity.Cyclicity,
		},
	}

	if response.Meta != nil {
		respDTO.Meta = &MetricsMetaResponseDTO{
			Source: response.Meta.Source,
			TimeMs: response.Meta.TimeMs,
		}
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(respDTO)
}
