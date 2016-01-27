package io.gd.generator;

import io.gd.generator.config.Config;
import io.gd.generator.context.JdbcContext;
import io.gd.generator.handler.MysqlHandler;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MysqlGenerator<T extends JdbcContext> extends AbstractGenerator<T> {

	static final Logger logger = LoggerFactory.getLogger(MysqlGenerator.class);

	protected Connection connection;

	public MysqlGenerator(Config config) {
		super(config);
		handlers.add(new MysqlHandler());
	}

	@Override
	protected void init() throws Exception {
		super.init();
		connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
	}

	@Override
	protected void destroy() throws Exception {
		if (connection != null) {
			connection.close();
		}
	}

	@Override
	protected void initContext(T context) {
		super.initContext(context);
		context.setConnection(connection);
	}

}
