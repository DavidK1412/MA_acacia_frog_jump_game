package com.acacia;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.runtime.Micronaut;

@Introspected(
        packages = "com.acacia.api.rest.dto",
        includedAnnotations = Introspected.class
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
