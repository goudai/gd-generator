package io.gd.generator.handler;

import io.gd.generator.Config;

@FunctionalInterface
public interface Handler {

	void start(Config config) throws Exception;



}
