package router

import (
	"graph-api/internal/api/handler"
	"net/http"

	"github.com/gorilla/mux"
)

type Router struct {
	muxRouter   *mux.Router
	pingHandler *handler.PingHandler
}

func NewRouter(pingHandler *handler.PingHandler) *Router {
	return &Router{
		muxRouter:   mux.NewRouter(),
		pingHandler: pingHandler,
	}
}

func (r *Router) Setup() http.Handler {
	r.muxRouter.HandleFunc("/ping", r.pingHandler.Handle).Methods("GET")
	return r.muxRouter
}
