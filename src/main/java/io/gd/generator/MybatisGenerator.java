package io.gd.generator;

import io.gd.generator.context.MybatisContext;
import io.gd.generator.handler.MybatisMapperHandler;
import io.gd.generator.handler.MybatisXmlHandler;
import io.gd.generator.handler.MysqlHandler;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisGenerator extends AbstractGenerator<MybatisContext> {

	static final Logger logger = LoggerFactory.getLogger(MybatisGenerator.class);
	
	private Connection connection;
	
	private String xmlPath;
	
	private String mapperPath;

	public MybatisGenerator(Config config) {
		super(config);
		handlers.add(new MybatisMapperHandler());
		handlers.add(new MybatisXmlHandler());
		handlers.add(new MysqlHandler());
	}
	
	@Override
	protected void init() throws Exception {
		super.init();
		connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
	}
	
	@Override
	protected void destroy() throws Exception {
		if(connection != null) {
			connection.close();
		}
	}

	@Override
	protected MybatisContext createContext(Class entityClass, Class queryModelClass) {
		MybatisContext mybatisContext = new MybatisContext();
		mybatisContext.setEntityClass(entityClass);
		mybatisContext.setQueryModelClass(queryModelClass);
		mybatisContext.setFreemarkerConfiguration(freemarkerConfiguration);
		mybatisContext.setConfig(config);
		return mybatisContext;
	}

}
