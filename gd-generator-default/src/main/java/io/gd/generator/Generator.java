package io.gd.generator;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import io.gd.generator.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;


public class Generator {

    static Logger logger = LoggerFactory.getLogger(Generator.class);

    static {
        try {
            LogBackConfigLoader.load();
        } catch (JoranException e) {
            e.printStackTrace();
        }
    }

    public static void generate(final Config config, Handler... hanlders) {

        Objects.requireNonNull(hanlders, "handlers must not be null");
        for (Handler handler : hanlders) {
            try {
                handler.start(config);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    public static class LogBackConfigLoader {

        public static void load() throws JoranException {
            final String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<configuration debug=\"true\">\n" +
                    "\t<appender name=\"STDOUT\" class=\"ch.qos.logback.core.ConsoleAppender\">\n" +
                    "\t\t<encoder>\n" +
                    "\t\t\t<charset>UTF-8</charset>\n" +
                    "\t\t\t<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>\n" +
                    "\t\t</encoder>\n" +
                    "\t</appender>\n" +
                    "\t<root level=\"INFO\">\n" +
                    "\t\t<appender-ref ref=\"STDOUT\"/>\n" +
                    "\t</root>\n" +
                    "</configuration>";
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            configurator.doConfigure(new ByteArrayInputStream(s.getBytes(Charset.forName("UTF-8"))));
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        }

    }

}
