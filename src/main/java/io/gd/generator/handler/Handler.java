package io.gd.generator.handler;

import io.gd.generator.Context;

/* T: meta object or String */
public interface Handler {

	void handle(Context context) throws Exception;

}
