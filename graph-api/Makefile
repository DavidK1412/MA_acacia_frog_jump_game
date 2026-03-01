SHELL := /bin/bash

.PHONY: help
help: ## Muestra esta ayuda
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: run
run: ## Ejecuta la aplicación
	go run main.go

.PHONY: build
build: ## Compila la aplicación
	go build -o bin/api main.go

.PHONY: test
test: ## Ejecuta los tests
	go test -v ./...

.PHONY: test-coverage
test-coverage: ## Ejecuta los tests con cobertura
	go test -coverprofile=coverage.out ./...
	go tool cover -html=coverage.out -o coverage.html

.PHONY: lint
lint: ## Ejecuta el linter
	golangci-lint run

.PHONY: fmt
fmt: ## Formatea el código
	go fmt ./...

.PHONY: tidy
tidy: ## Limpia las dependencias
	go mod tidy

.PHONY: deps
deps: ## Descarga las dependencias
	go mod download

.PHONY: clean
clean: ## Limpia archivos generados
	rm -rf bin/
	rm -f coverage.out coverage.html

.PHONY: dev
dev: ## Ejecuta la aplicación en modo desarrollo con hot reload (requiere air)
	air

.DEFAULT_GOAL := help
