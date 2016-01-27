package io.gd.generator.context;

import java.sql.Connection;

public class JdbcContext extends Context {

	protected Connection connection;

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
