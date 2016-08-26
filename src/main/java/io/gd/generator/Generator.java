package io.gd.generator;

import io.gd.generator.handler.Handler;

import java.util.Objects;

public class Generator {
	
	@SafeVarargs
	public static <T extends Handler> void generate(Config config, Class<T>... hanlderClasses) throws Exception {
		Objects.requireNonNull(hanlderClasses, "handler classes must not be null");
		for (Class<T> hanlderClass : hanlderClasses) {
			Handler hanlder = hanlderClass.newInstance();
			hanlder.handle(config);
		}
	}

}
