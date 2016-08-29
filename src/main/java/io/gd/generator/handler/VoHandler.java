package io.gd.generator.handler;

import freemarker.template.TemplateException;
import io.gd.generator.api.vo.View;
import io.gd.generator.api.vo.ViewIgnore;
import io.gd.generator.api.vo.ViewObject;
import io.gd.generator.util.ClassHelper;
import io.gd.generator.util.ConfigChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.gd.generator.util.ClassHelper.getFields;
import static java.io.File.separator;
import static java.util.Arrays.stream;

/**
 * Created by freeman on 16/8/29.
 */
public class VoHandler extends AbstractHandler {


	private String voPackage;
	private String dir;
	private boolean useLombok;

	public VoHandler(String voPackage,String dir,boolean useLombok) {
		this.voPackage= voPackage;
		if(voPackage == null || "".equals(voPackage))
			throw new NullPointerException("voPackage  does nou be null");
		this.dir = dir;
		if(dir == null || "".equals(dir))
			throw new NullPointerException("VO dir does nou be null");
		this.useLombok = useLombok;
	}

	@Override
	protected void init() throws Exception {
		super.init();
		ConfigChecker.notBlank(dir, "config queryModelSuffix is miss");
		/* 初始化文件夹 */
		File path = new File(dir);
		if (!path.exists()) {
			path.mkdirs();
		} else if (!path.isDirectory()) {
			throw new IllegalArgumentException("queryModelPath is not a directory");
		}
	}

	@Override
	protected void doHandle(Set<Class<?>> entityClasses) {
		Map<String, Meta> groupClassMap = init(entityClasses, initGroups(entityClasses));
			groupClassMap.forEach((k,v)->{
				try {
					final HashMap<String, Object> meta = new HashMap<String, Object>() {{
						put("meta", v);
					}};
					final String vo = renderTemplate("vo", meta);
					File file = new File(dir+separator+v.className+".java");
					if(file.exists()) file.delete();
					file.createNewFile();
					try (FileOutputStream os = new FileOutputStream(file)) {
						os.write(vo.getBytes());
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TemplateException e) {
					e.printStackTrace();
				}
			});




	}

	private Map<String, Meta> init(Set<Class<?>> entityClasses, Map<String, Meta> groupClassMap) {
		entityClasses.forEach(entityClass -> {
			if (entityClass.isAnnotationPresent(ViewObject.class)) {
				final ViewObject viewObject = entityClass.getDeclaredAnnotation(ViewObject.class);
				//handle class
				stream(viewObject.views()).forEach(view -> {
					stream(view.group()).forEach(group -> {
						final Meta meta = groupClassMap.get(group);
						meta.imports.add(view.type().getName());
						final Meta.Field field = new Meta.Field();
						field.paradigm = view.elementGroup();
						field.type = view.type().getSimpleName();
						field.name = view.name();
						meta.getFields().add(field);
					});
				});
				// handle fileds
				getFields(entityClass).stream()
						.filter(ClassHelper::isNotStaticField)
						.filter(field -> !field.isAnnotationPresent(ViewIgnore.class))
						.forEach(field -> {
							final View view = field.getDeclaredAnnotation(View.class);
							if (view == null) {
								stream(viewObject.groups()).forEach(group -> {
									final Meta meta = groupClassMap.get(group);
									final Meta.Field f = new Meta.Field();
									f.type = field.getType().getSimpleName();
									f.name = field.getName();
									if(!field.getType().getName().startsWith("java.lang")){
										if(field.getType().getName().contains("$")){
											meta.imports.add("static "+field.getType().getName().replace("$","."));
										}else{
											meta.imports.add(field.getType().getName());
										}
									}
									meta.getFields().add(f);
								});
							}else{
								stream(viewObject.groups()).forEach(group -> {
									final Meta meta = groupClassMap.get(group);
									final Meta.Field f = new Meta.Field();
									f.name = (view.name()!=null && !view.name().equals(""))?view.name():field.getName();
									f.type = field.getType().getSimpleName();
									f.paradigm = view.elementGroup();
									if(!field.getType().getName().startsWith("java.lang")){
										if(field.getType().getName().contains("$")){
											meta.imports.add("static "+field.getType().getName().replace("$","."));
										}else{
											meta.imports.add(field.getType().getName());
										}
									}
									meta.getFields().add(f);
								});
							}
						});
			}

		});

		return groupClassMap;
	}

	private Map<String, Meta> initGroups(Set<Class<?>> entityClasses) {
		Map<String, Meta> groupClassMap = new HashMap<>();
		entityClasses.forEach(entityClasse -> {
			if (entityClasse.isAnnotationPresent(ViewObject.class)) {
				final ViewObject vo = entityClasse.getDeclaredAnnotation(ViewObject.class);
				stream(vo.groups()).forEach(className -> {
					final Meta value = new Meta();
					value.className = className;
					value.voPackage = voPackage;
					value.useLombok = this.useLombok;
					groupClassMap.put(className, value);
				});
			}
		});
		return groupClassMap;
	}

	public static class Meta {
		private Set<String> imports = new HashSet<>();
		private Set<Field> fields = new HashSet<>();
		private String voPackage;
		private String className;
		private boolean useLombok;


		public static class Field{
			private String name;
			private String paradigm = "";
			private String type;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getParadigm() {
				if(paradigm == null || "".equals(paradigm)) return "";
				return "<"+paradigm+">";
			}

			public void setParadigm(String paradigm) {
				this.paradigm = paradigm;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			@Override
			public String toString() {
				return "Field{" +
						"name='" + name + '\'' +
						", paradigm='" + paradigm + '\'' +
						", type='" + type + '\'' +
						'}';
			}
		}

		public Set<String> getImports() {
			return imports;
		}

		public void setImports(Set<String> imports) {
			this.imports = imports;
		}

		public Set<Field> getFields() {
			return fields;
		}

		public void setFields(Set<Field> fields) {
			this.fields = fields;
		}

		public String getVoPackage() {
			return voPackage;
		}

		public void setVoPackage(String voPackage) {
			this.voPackage = voPackage;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public boolean isUseLombok() {
			return useLombok;
		}

		public void setUseLombok(boolean useLombok) {
			this.useLombok = useLombok;
		}

		@Override
		public String toString() {
			return "Meta{" +
					"imports=" + imports +
					", fields=" + fields +
					", voPackage='" + voPackage + '\'' +
					", className='" + className + '\'' +
					'}';
		}
	}


}


