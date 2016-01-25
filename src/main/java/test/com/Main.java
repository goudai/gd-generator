package test.com;

import io.gd.generator.Config;
import io.gd.generator.Generator;
import io.gd.generator.MybatisGenerator;

public class Main {
	public static void main(String[] S) {
		Config config = new Config();
		config.setUrl("jdbc:mysql://127.0.0.1/ke");
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
