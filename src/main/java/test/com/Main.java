package test.com;

import io.gd.generator.Config;
import io.gd.generator.Generater;

import java.io.File;

public class Main {
	public static void main(String[] S) {
		Config config = new Config();
/*		config.setEntityPackage("com.ke.entity");
		config.setMapperPackage("com.ke.mapper");
		config.setQueryModelPackage("com.ke.model.query");
		config.setXMLPackage("com.mapping");*/
		config.setUrl("jdbc:mysql://192.168.1.66/test");
		config.setEntityPackage("test.com.entity");
		config.setMapperPackage("test.com.mapper");
		config.setQueryModelPackage("test.com.model.query");
		config.setXMLPackage("test.com.mapping");
		config.setUsername("root");
		config.setPassword("123456");
		Generater.run(config,new File("D:/LOGS3.LOG"));
	}

}
