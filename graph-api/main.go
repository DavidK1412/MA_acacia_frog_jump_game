package main

import (
	"context"
	"fmt"
	"graph-api/internal/api/handler"
	"graph-api/internal/api/router"
	"graph-api/internal/application/health/usecase"
	"graph-api/internal/infrastructure/config"
	"graph-api/internal/infrastructure/repository"
	"graph-api/pkg/logger"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"go.uber.org/zap"
)

func main() {
	log, err := logger.NewZapLogger()
	if err != nil {
		fmt.Printf("Error inicializando logger: %v\n", err)
		os.Exit(1)
	}
	defer log.Sync()

	cfg := config.LoadConfig()

	log.Info("Iniciando aplicación",
		zap.String("port", cfg.Server.Port),
	)

	healthRepo := repository.NewInMemoryHealthRepository()

	pingUseCase := usecase.NewPingUseCase(healthRepo)

	pingHandler := handler.NewPingHandler(pingUseCase)

	r := router.NewRouter(pingHandler)

	srv := &http.Server{
		Addr:         ":" + cfg.Server.Port,
		Handler:      r.Setup(),
		ReadTimeout:  15 * time.Second,
		WriteTimeout: 15 * time.Second,
		IdleTimeout:  60 * time.Second,
	}

	go func() {
		log.Info("Servidor HTTP iniciado",
			zap.String("address", srv.Addr),
		)
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatal("Error iniciando servidor", zap.Error(err))
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Info("Apagando servidor...")

	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		log.Fatal("Error al apagar servidor", zap.Error(err))
	}

	log.Info("Servidor detenido correctamente")
}