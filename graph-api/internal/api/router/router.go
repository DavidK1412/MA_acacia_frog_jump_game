package router

import (
	"graph-api/internal/api/handler"
	"net/http"

	"github.com/gorilla/mux"
)

type Router struct {
	muxRouter        *mux.Router
	pingHandler      *handler.PingHandler
	nextMoveHandler  *handler.NextMoveHandler
}

func NewRouter(pingHandler *handler.PingHandler, nextMoveHandler *handler.NextMoveHandler) *Router {
	return &Router{
		muxRouter:       mux.NewRouter(),
		pingHandler:     pingHandler,
		nextMoveHandler: nextMoveHandler,
	}
}

func (r *Router) Setup() http.Handler {
	r.muxRouter.HandleFunc("/ping", r.pingHandler.Handle).Methods("GET")
	r.muxRouter.HandleFunc("/v1/graph/next-move", r.nextMoveHandler.Handle).Methods("POST")
	return r.muxRouter
}
