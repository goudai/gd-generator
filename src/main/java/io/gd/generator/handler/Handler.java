package io.gd.generator.handler;

import io.gd.generator.context.Context;

public interface Handler<T extends Context> {

	void handle(T context) throws Exception;

}
