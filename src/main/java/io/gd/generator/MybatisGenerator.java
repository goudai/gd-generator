package io.gd.generator;

import io.gd.generator.handler.MybatisMapperHandler;
import io.gd.generator.handler.MybatisXmlHandler;
import io.gd.generator.handler.MysqlHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisGenerator extends AbstractGenerator {

	static final Logger logger = LoggerFactory.getLogger(MybatisGenerator.class);

	public MybatisGenerator(Config config) {
		super(config);
		handlers.add(new MybatisMapperHandler());
		handlers.add(new MybatisXmlHandler());
		handlers.add(new MysqlHandler());
	}

}
