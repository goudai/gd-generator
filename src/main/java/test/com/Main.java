package test.com;

import io.gd.generator.Generator;
import io.gd.generator.MybatisGenerator;
import io.gd.generator.config.Config;

public class Main {
	public static void main(String[] S) {
		Config config = new Config();
		config.setGenLogFile("C:/Users/Administrator/gd-test.log");
		config.setUrl("jdbc:mysql://127.0.0.1/test");
		config.setEntityPackage("test.com.entity");
		config.setMybatisMapperPackage("test.com.mapper");
		config.setQueryModelPackage("test.com.model.query");
		config.setMybatisXmlPackage("test.com.mapping");
		config.setUsername("root");
		config.setPassword("123456");
		Generator generator = new MybatisGenerator(config);
		generator.generate();
	}

}
