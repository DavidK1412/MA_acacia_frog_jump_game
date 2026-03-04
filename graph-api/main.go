package main

import (
	"context"
	"fmt"
	"graph-api/internal/api/handler"
	"graph-api/internal/api/router"
	gameusecase "graph-api/internal/application/game/usecase"
	healthusecase "graph-api/internal/application/health/usecase"
	"graph-api/internal/domain/game/policy"
	"graph-api/internal/domain/game/service"
	"graph-api/internal/infrastructure/config"
	"graph-api/internal/infrastructure/repository"
	"graph-api/pkg/logger"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
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

	var dbPool *pgxpool.Pool

	if cfg.Database.URL != "" {
		var err error
		dbPool, err = pgxpool.New(context.Background(), cfg.Database.URL)
		if err != nil {
			log.Fatal("Error conectando a PostgreSQL", zap.Error(err))
		}
		defer dbPool.Close()

		if err := dbPool.Ping(context.Background()); err != nil {
			log.Fatal("Error verificando conexión a PostgreSQL", zap.Error(err))
		}

		log.Info("Conexión a PostgreSQL establecida")
	} else {
		log.Warn("DATABASE_URL no configurado, endpoint /v1/graph/metrics no estará disponible")
	}

	policyLoader := policy.NewPolicyLoader(true)
	log.Info("Policy tables cargadas",
		zap.String("source", policyLoader.LoadSource()),
	)

	healthRepo := repository.NewInMemoryHealthRepository()

	pingUseCase := healthusecase.NewPingUseCase(healthRepo)

	nextMoveUseCase := gameusecase.NewNextMoveUseCase(policyLoader)

	legalMovesGenerator := service.NewLegalMovesGenerator()

	var metricsUseCase *gameusecase.MetricsUseCase
	if dbPool != nil {
		movementRepo := repository.NewPostgresMovementRepository(dbPool)
		metricsUseCase = gameusecase.NewMetricsUseCase(movementRepo, legalMovesGenerator)
	}

	pingHandler := handler.NewPingHandler(pingUseCase)
	nextMoveHandler := handler.NewNextMoveHandler(nextMoveUseCase)
	
	var metricsHandler *handler.MetricsHandler
	if metricsUseCase != nil {
		metricsHandler = handler.NewMetricsHandler(metricsUseCase)
	}

	r := router.NewRouter(pingHandler, nextMoveHandler, metricsHandler)

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