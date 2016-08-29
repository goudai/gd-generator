package io.gd.generator;

import io.gd.generator.handler.Handler;

import java.util.Arrays;
import java.util.Objects;

public class Generator {

	public static void generate(final Config config, Handler... hanlderClasses) throws Exception {
		Objects.requireNonNull(hanlderClasses, "handler classes must not be null");
		Arrays.stream(hanlderClasses).forEach(handler -> {
			try {
				handler.start(config);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
