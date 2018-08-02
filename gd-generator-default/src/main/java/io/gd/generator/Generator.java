package io.gd.generator;

import io.gd.generator.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;



public class Generator {

   static Logger logger = LoggerFactory.getLogger(Generator.class);
    public static void generate(final Config config, Handler... hanlders) {

        Objects.requireNonNull(hanlders, "handlers must not be null");
        for (Handler handler : hanlders) {
            try {
                handler.start(config);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }

    }

}
