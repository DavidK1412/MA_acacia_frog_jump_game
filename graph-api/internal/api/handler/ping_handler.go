package handler

import (
	"encoding/json"
	"graph-api/internal/application/health/usecase"
	"net/http"
)

type PingHandler struct {
	pingUseCase *usecase.PingUseCase
}

func NewPingHandler(pingUseCase *usecase.PingUseCase) *PingHandler {
	return &PingHandler{
		pingUseCase: pingUseCase,
	}
}

type PingResponse struct {
	Status    string `json:"status"`
	Message   string `json:"message"`
	Timestamp string `json:"timestamp"`
}

func (h *PingHandler) Handle(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	health, err := h.pingUseCase.Execute(ctx)
	if err != nil {
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}

	response := PingResponse{
		Status:    health.Status(),
		Message:   health.Message(),
		Timestamp: health.Timestamp().Format("2006-01-02T15:04:05Z07:00"),
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(response)
}
