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
import java.lang.reflect.Field;
import java.util.*;

import static io.gd.generator.util.ClassHelper.getFields;
import static io.gd.generator.util.StringUtils.isNotBlank;
import static io.gd.generator.util.StringUtils.replaceFirstToLower;
import static java.io.File.separator;
import static java.util.Arrays.stream;

/**
 * Created by freeman on 16/8/29.
 */
public class VoHandler extends AbstractHandler {


	private String voPackage;
	private String dir;
	private boolean useLombok;

	public VoHandler(String voPackage, String dir, boolean useLombok) {
		this.voPackage = voPackage;
		if (voPackage == null || "".equals(voPackage))
			throw new NullPointerException("voPackage  does nou be null");
		this.dir = dir;
		if (dir == null || "".equals(dir))
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
	protected void doHandleOne(Class<?> entityClass) throws Exception {
		if (entityClass.isAnnotationPresent(ViewObject.class)) {
			final ViewObject viewObject = entityClass.getDeclaredAnnotation(ViewObject.class);
			Map<String, Meta> result = new HashMap<>();
			//生成类元数据
			Arrays.stream(viewObject.groups()).forEach(voName -> {
				final Meta meta = new Meta();
				meta.className = voName;
				meta.voPackage = voPackage;
				meta.useLombok = useLombok;
				result.put(voName, meta);
			});
			for (View view : viewObject.views()) {
				for (String group : view.groups()) {
					final Meta meta = result.get(group);
					if (meta == null)
						throw new NullPointerException("group " + group + "未在类 " + entityClass.getName() + "上声明");
					final Meta.Field field = new Meta.Field();
					switch (view.elementGroupType()) {
						case ASSOCIATION:
							field.type = view.elementGroup();
							field.name = isNotBlank(view.name()) ? replaceFirstToLower(view.elementGroup()) : view.name();
							meta.imports.add(voPackage + "." + view.elementGroup());
							break;
						case COLLECTION:
							if (!Collection.class.isAssignableFrom(view.type()))
								throw new IllegalArgumentException("view type is " + view.type().getName() + " must be collection subclasses");
							final String name = view.name();
							if (isNotBlank(name))
								throw new NullPointerException("type is collection ,view name must be not null");
							field.name = name;
							field.paradigm = view.elementGroup();
							field.type = view.type().getSimpleName();
							meta.imports.add(view.type().getName());
							meta.imports.add(voPackage + "." + view.elementGroup());
							break;
						case SIMPLE:
							throw new UnsupportedOperationException("view class not support SIMPLE");
						case MAP:
							//TODO
							throw new UnsupportedOperationException("elementGroup type map");
					}
					meta.fields.add(field);
				}
			}

			for (Field f : getFields(entityClass)) {
				if(f.isAnnotationPresent(View.class)){
					final View view = f.getDeclaredAnnotation(View.class);
					for (String group : view.groups()) {
						final Meta meta = result.get(group);
						if (meta == null)
							throw new NullPointerException("group " + group + "未在类 " + entityClass.getName() + "上声明");
						final Meta.Field field = new Meta.Field();
						switch (view.elementGroupType()) {
							case ASSOCIATION:
								field.type = view.elementGroup();
								field.name = isNotBlank(view.name()) ? replaceFirstToLower(view.elementGroup()) : view.name();
								meta.imports.add(voPackage + "." + view.elementGroup());
								break;
							case COLLECTION:
								if (!Collection.class.isAssignableFrom(view.type()))
									throw new IllegalArgumentException("view type is " + view.type().getName() + " must be collection subclasses");
								final String name = view.name();
								if (isNotBlank(name))
									throw new NullPointerException("type is collection ,view name must be not null");
								field.name = name;
								field.paradigm = view.elementGroup();
								field.type = view.type().getSimpleName();
								meta.imports.add(view.type().getName());
								meta.imports.add(voPackage + "." + view.elementGroup());
								break;
							case SIMPLE:
//								field.name =
								break;
							case MAP:
								//TODO
								throw new UnsupportedOperationException("elementGroup type map");
						}
						meta.fields.add(field);
					}
				}

			}

		}
	}

	@Override
	protected void doHandle(Set<Class<?>> entityClasses) {
		Map<String, Meta> groupClassMap = init(entityClasses, initGroups(entityClasses));
		groupClassMap.forEach((k, v) -> {
			try {
				final HashMap<String, Object> meta = new HashMap<String, Object>() {{
					put("meta", v);
				}};
				final String vo = renderTemplate("vo", meta);
				File file = new File(dir + separator + v.className + ".java");
				if (file.exists()) file.delete();
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
					stream(view.groups()).forEach(group -> {
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
							final View[] views = field.getAnnotationsByType(View.class);
							if (views == null) {
								stream(viewObject.groups()).forEach(group -> {
									final Meta meta = groupClassMap.get(group);
									final Meta.Field f = new Meta.Field();
									f.type = field.getType().getSimpleName();
									f.name = field.getName();
									if (!field.getType().getName().startsWith("java.lang")) {
										if (field.getType().getName().contains("$")) {
											meta.imports.add("static " + field.getType().getName().replace("$", "."));
										} else {
											meta.imports.add(field.getType().getName());
										}
									}
									meta.getFields().add(f);
								});
							} else {
								stream(views).forEach(view1 -> {
									doHandleView(groupClassMap, viewObject, field, view1);
								});

							}
						});
			}

		});

		return groupClassMap;
	}

	private void doHandleView(Map<String, Meta> groupClassMap, ViewObject viewObject, Field field, View view) {
		String[] groups = view.groups();
		if (groups.length == 0) {
			groups = viewObject.groups();
		}
		stream(groups).forEach(group -> {
			Meta meta = groupClassMap.get(group);
			if (meta == null) {
				meta = new Meta();
				meta.className = group;
				meta.voPackage = voPackage;
				meta.useLombok = this.useLombok;
				groupClassMap.put(group, meta);
			}
			final Meta.Field f = new Meta.Field();
			f.name = (view.name() != null && !view.name().equals("")) ? view.name() : field.getName();
			if (view.name() == null || "".equals(view.name())) {
				f.type = field.getType().getSimpleName();
			} else {
				f.type = view.type().getSimpleName();
			}
			f.paradigm = view.elementGroup();
			if (!field.getType().getName().startsWith("java.lang")) {
				if (field.getType().getName().contains("$")) {
					meta.imports.add("static " + field.getType().getName().replace("$", "."));
				} else {
					meta.imports.add(field.getType().getName());
				}
			}
			meta.getFields().add(f);
		});
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


		public static class Field {
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
				if (paradigm == null || "".equals(paradigm)) return "";
				return "<" + paradigm + ">";
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


