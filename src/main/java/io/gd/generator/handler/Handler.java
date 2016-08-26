package io.gd.generator.handler;

import io.gd.generator.Config;


public interface Handler {

	void handle(Config config) throws Exception;

}
