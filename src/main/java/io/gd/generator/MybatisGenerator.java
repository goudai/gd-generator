package io.gd.generator;

import io.gd.generator.handler.MybatisMapperHandler;
import io.gd.generator.handler.MybatisXmlHandler;
import io.gd.generator.handler.MysqlHandler;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisGenerator extends AbstractGenerator {

	static final Logger logger = LoggerFactory.getLogger(MybatisGenerator.class);
	
	private Connection connection;

	public MybatisGenerator(Config config) {
		super(config);
		handlers.add(new MybatisMapperHandler());
		handlers.add(new MybatisXmlHandler());
		handlers.add(new MysqlHandler());
	}
	
	@Override
	protected void init() throws Exception {
		super.init();
		connection = null;
	}
	
	@Override
	protected void destroy() throws Exception {
		
	}

}
