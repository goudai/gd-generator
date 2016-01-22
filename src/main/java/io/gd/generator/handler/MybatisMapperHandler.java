package io.gd.generator.handler;

import freemarker.template.Template;
import io.gd.generator.Config;
import io.gd.generator.Context;
import io.gd.generator.meta.mybatis.mapper.MybatisMapperMeta;
import io.gd.generator.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MybatisMapperHandler implements Handler<String> {

	@Override
	public String read(Context context) throws Exception {
		Config config = context.getConfig();
		String string = config.getMybatisMapperPath() + File.separator + mapperName;
		File file = new File(string);
		StringBuilder b = new StringBuilder();
		String imp = "";
		if (file.isFile()) {
			String fileToString = this.getFileToString(file);
		}
		return fileToString;
	}

	@Override
	public String parse(Context context) throws Exception {
		Class<?> entityClass = context.getEntityClass();
		Class<?> queryModelClass = context.getQueryModelClass();
		MybatisMapperMeta mmm = null;
		mmm = new MybatisMapperMeta();
		if (queryModelClass != null) {
			mmm.setQueryModel(queryModelClass.getName());
			mmm.setSimpleQuery(queryModelClass.getSimpleName());
		}
		mmm.setModelName(entityClass.getName());
		mmm.setSimpleName(entityClass.getSimpleName());
		Map<String, Object> model = new HashMap<>();
		model.put("basePackage", mapperPackage);
		model.put("mmm", mmm);
		StringWriter out = new StringWriter();
		Template template = context.getFreemarkerConfiguration().getTemplate("mybatisMapper.ftl");
		template.process(model, out);
		return out.toString();
	}

	@Override
	public String merge(String parsed, String read, Context context) throws Exception {
		if (StringUtils.isNotBank(fileToString)) {
			String[] split2 = fileToString.split("\\{");
			imp = split2[0];
			String[] split = split2[1].split("\\}")[0].split(";");
			Arrays.asList(split).forEach(
					(m) -> {
						if (!(m.contains("insert(") || m.contains("update(") || m.contains("findOne(") || m.contains("findAll(") || m.contains("merge(")
								|| m.contains("count(") || m.contains("delete("))) {
							if (StringUtils.isNotBank(m.trim())) {
								if (!";".equals(m.trim()) && !"\\t".equals(m.trim())) {
									if (mapperName.contains("AdminMapper")) {
										System.out.println();
									}
									b.append("\r\n\t" + (m + ";\r\n").trim());
								}
							}
						}
					});
		}
		if (StringUtils.isNotBank(b.toString())) {
			mapper = mapper.split("\\}")[0] + b.toString() + "\r\n }";
		}
		if (StringUtils.isNotBank(imp))
			mapper = imp + "{" + mapper.split("\\{")[1];
		return null;
	}

	@Override
	public void write(String merged, Context context) throws Exception {
		String string = this.mapper + File.separator + mapperName;
		File file = new File(string);
		StringBuilder b = new StringBuilder();
		String imp = "";
		if (file.isFile()) {
			String fileToString = this.getFileToString(file);
			if (StringUtils.isNotBank(fileToString)) {
				String[] split2 = fileToString.split("\\{");
				imp = split2[0];
				String[] split = split2[1].split("\\}")[0].split(";");
				Arrays.asList(split).forEach(
						(m) -> {
							if (!(m.contains("insert(") || m.contains("update(") || m.contains("findOne(") || m.contains("findAll(") || m.contains("merge(")
									|| m.contains("count(") || m.contains("delete("))) {
								if (StringUtils.isNotBank(m.trim())) {
									if (!";".equals(m.trim()) && !"\\t".equals(m.trim())) {
										if (mapperName.contains("AdminMapper")) {
											System.out.println();
										}
										b.append("\r\n\t" + (m + ";\r\n").trim());
									}
								}
							}
						});
			}
			file.delete();
		}
		createFile(file);
		if (StringUtils.isNotBank(b.toString())) {
			mapper = mapper.split("\\}")[0] + b.toString() + "\r\n }";
		}
		if (StringUtils.isNotBank(imp))
			mapper = imp + "{" + mapper.split("\\{")[1];
		try (FileOutputStream os = new FileOutputStream(file)) {
			System.out.println("写入" + mapperName);
			os.write(mapper.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

}
