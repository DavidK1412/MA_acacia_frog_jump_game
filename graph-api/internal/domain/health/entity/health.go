package entity

import "time"

type Health struct {
	status    string
	timestamp time.Time
	message   string
}

func NewHealth(status, message string) *Health {
	return &Health{
		status:    status,
		timestamp: time.Now(),
		message:   message,
	}
}

func (h *Health) Status() string {
	return h.status
}

func (h *Health) Timestamp() time.Time {
	return h.timestamp
}

func (h *Health) Message() string {
	return h.message
}

func (h *Health) IsHealthy() bool {
	return h.status == "healthy"
}
