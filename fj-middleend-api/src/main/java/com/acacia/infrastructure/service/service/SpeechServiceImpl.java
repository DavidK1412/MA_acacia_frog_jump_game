package com.acacia.infrastructure.service.service;

import com.acacia.app.domain.entity.game.decision.Decision;
import com.acacia.app.domain.services.external.speech.SpeechService;
import com.acacia.infrastructure.client.dto.graph.NextMove;
import com.acacia.infrastructure.client.dto.graph.NextMoveResponse;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Singleton
public class SpeechServiceImpl implements SpeechService {

    private final Map<String, Function<Object, String>> actions = Map.of(
            Decision.DecisionType.BEST_NEXT, this::getBestNextText
    );

    @Override
    public String getTextForAction(String action, Object context) {
        Function<Object, String> textFunction = actions.get(action);
        if (textFunction == null) {
            throw new IllegalArgumentException("No text function found for action: " + action);
        }
        return textFunction.apply(context);
    }


    private String getBestNextText(Object context) {
        NextMove ctx = ((NextMoveResponse) context).nextMove();
        return SpeechTemplateGenerator.generate(
                ctx.frogId(),
                ctx.fromIndex(),
                ctx.toIndex()
        );
    }

    private static class SpeechTemplateGenerator {

        private static final List<String> TEMPLATES = List.of(
                "Vamos bien. Mueve el cubo {cubeId} de {from} a {to}. Ese cubo ya quería pasear.",
                "Intenta esto: lleva el cubo {cubeId} de {from} a {to}. Fácil.",
                "Pequeño reto: mueve el cubo {cubeId} de {from} a {to}. Yo creo que puedes.",
                "Ese cubo se ve aburrido. Muévelo de {from} a {to}.",
                "El cubo {cubeId} quiere cambiar de casa. Llévalo de {from} a {to}.",
                "Mueve el cubo {cubeId} de {from} a {to}. Prometo que no se mareará.",
                "El cubo {cubeId} necesita ejercicio. Muévelo de {from} a {to}.",
                "Buen intento. Ahora mueve el cubo {cubeId} de {from} a {to}.",
                "Mira bien: el cubo {cubeId} puede ir de {from} a {to}.",
                "Vamos paso a paso. Mueve el cubo {cubeId} de {from} a {to}.",
                "El cubo {cubeId} dice: '¡sácame de {from}!'. Llévalo a {to}.",
                "Ese cubo tiene espíritu aventurero. De {from} a {to}.",
                "Cubo {cubeId} en misión secreta: salir de {from} y llegar a {to}.",
                "Pequeña misión: mover el cubo {cubeId} de {from} a {to}. Nivel fácil."
        );

        public static String generate(Integer cubeId, Integer from, Integer to) {
            String template = TEMPLATES.get(
                    ThreadLocalRandom.current().nextInt(TEMPLATES.size())
            );

            return template
                    .replace("{cubeId}", cubeId.toString())
                    .replace("{from}", String.valueOf(from))
                    .replace("{to}", String.valueOf(to));
        }
    }
}
