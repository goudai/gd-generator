package io.gd.generator;

import io.gd.generator.handler.Handler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;


@Slf4j
public class Generator {

    public static void generate(final Config config, Handler... hanlderClasses) {
        Objects.requireNonNull(hanlderClasses, "handler classes must not be null");
        Arrays.stream(hanlderClasses).forEach(handler -> {
            try {
                handler.start(config);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
