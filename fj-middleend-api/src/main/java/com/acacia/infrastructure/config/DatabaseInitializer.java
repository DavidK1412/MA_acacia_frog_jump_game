package com.acacia.infrastructure.config;

import com.acacia.app.domain.entity.game.difficulty.Difficulty;
import com.acacia.app.domain.services.external.difficulty.DifficultyService;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements ApplicationEventListener<ServerStartupEvent> {
    
    private final DifficultyService difficultyService;
    
    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        log.info("Inicializando datos de la base de datos...");
        initializeDifficulties();
        log.info("Inicialización de datos completada.");
    }
    
    private void initializeDifficulties() {
        if (!difficultyService.findAll().isEmpty()) {
            log.info("Los datos de dificultad ya existen. Saltando inicialización.");
            return;
        }
        
        Difficulty easy = Difficulty.builder()
                .name("easy")
                .numberOfBlocks(7)
                .build();
        difficultyService.save(easy);
        log.info("Nivel de dificultad 'easy' creado con 7 bloques.");
        
        Difficulty medium = Difficulty.builder()
                .name("medium")
                .numberOfBlocks(9)
                .build();
        difficultyService.save(medium);
        log.info("Nivel de dificultad 'medium' creado con 9 bloques.");
        
        Difficulty hard = Difficulty.builder()
                .name("hard")
                .numberOfBlocks(12)
                .build();
        difficultyService.save(hard);
        log.info("Nivel de dificultad 'hard' creado con 12 bloques.");
    }
}
