package io.gd.generator.handler;

import io.gd.generator.annotation.view.*;
import io.gd.generator.util.ClassHelper;
import io.gd.generator.util.ConfigChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

import static io.gd.generator.util.ClassHelper.getFields;
import static io.gd.generator.util.StringUtils.isBlank;
import static io.gd.generator.util.StringUtils.isNotBlank;
import static java.io.File.separator;
import static java.lang.String.format;

/**
 * Created by freeman on 16/8/29.
 */
public class VoHandler extends AbstractHandler {


	private String voPackage;
	private String dir;
	private boolean useLombok;
	private Predicate<String> predicate = v -> true;


	public VoHandler(String voPackage, String dir, boolean useLombok, Predicate<String> predicate) {
		this.voPackage = voPackage;
		this.dir = dir;
		this.useLombok = useLombok;
		this.predicate = predicate;
	}

	public VoHandler(String voPackage, String dir, boolean useLombok) {
		this.voPackage = voPackage;
		if (voPackage == null || "".equals(voPackage))
			throw new NullPointerException("voPackage  does nou be null");
		this.dir = dir;
		if (dir == null || "".equals(dir))
			throw new NullPointerException("VO dir does nou be null");
		this.useLombok = useLombok;
	}

	public void setPredicate(Predicate<String> predicate) {
		this.predicate = predicate;
	}

	@Override
	protected void init() throws Exception {
		super.init();
		ConfigChecker.notBlank(dir, "vo path is miss");
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
			Map<String, Meta> result = handleClass(entityClass);
			doWrite(handleField(entityClass, result));
		}
	}

	private Map<String, Meta> handleClass(Class<?> entityClass) {
		final ViewObject viewObject = entityClass.getDeclaredAnnotation(ViewObject.class);
		final String[] groups = viewObject.groups();
		List<String> groupList = new ArrayList<>();
		for (String group : groups) {
			if (predicate.test(group)) {
				groupList.add(group);
			}
		}
		final String[] groups1 = groupList.toArray(new String[groupList.size()]);
		Map<String, Meta> result = initMeta(groups1);

		handleClassView(entityClass, viewObject.views(), result, groups1);

		handleClassAssociationView(entityClass, viewObject.associationViews(), result, groups1);

		handleClassCollectionView(entityClass, viewObject.collectionViews(), result, groups1);

		handleClassMapView(entityClass, viewObject.mapViews(), result, groups1);

		return result;


	}

	private Map<String, Meta> handleField(Class<?> entityClass, Map<String, Meta> result) {
		final ViewObject viewObject = entityClass.getDeclaredAnnotation(ViewObject.class);
		final String[] groups = viewObject.groups();
		getFields(entityClass).stream().filter(ClassHelper::isNotStaticField).forEach(field -> {

			handleFieldView(entityClass, result, groups, field);

			handleAssociationView(entityClass, result, groups, field);

			handleFieldCollectionView(entityClass, result, groups, field);

			handleFieldMapView(entityClass, result, groups, field);

		});

		return result;

	}

	private void handleFieldMapView(Class<?> entityClass, Map<String, Meta> result, String[] groups, Field field) {
		final MapView[] views = field.getAnnotationsByType(MapView.class);
		for (MapView view : views) {
			String[] viewGroups = view.groups();
			if (viewGroups.length == 0) {
				viewGroups = groups.clone();
			}
			for (String group : viewGroups) {
				if (predicate.test(group)) {
					final String entityClassName = entityClass.getName();
					final String format = format("%s 类的属性 %s 上使用注解@MapView，注解的name属性不能为空", entityClassName, field.getName());
					final io.gd.generator.annotation.Field viewField = view.field();

					final String label = handleFieldLabel(field, viewField.label(),
							format("%s 类中的字段 %s 上使用@MapView时,@Field的label属性为空", entityClass.getName(), field.getName()));

					doHandleMapView(entityClass, result, view, group, entityClassName, format, field.getType(), label, viewField.order());

				}

			}
		}
	}

	private void handleFieldCollectionView(Class<?> entityClass, Map<String, Meta> result, String[] groups, Field field) {
		final CollectionView[] views = field.getAnnotationsByType(CollectionView.class);
		if (views.length > 0) {
			for (CollectionView view : views) {
				String[] viewGroups = view.groups();
				if (viewGroups.length == 0) {
					viewGroups = groups.clone();
				}
				for (String group : viewGroups) {
					if (predicate.test(group)) {
						final String entityClassName = entityClass.getName();
						final Meta meta = metaCheck(entityClass, result, group);
						final String name = view.name();
						final Class<?> type = view.type();
						if (isBlank(name)) {
							throw new NullPointerException(format("%s 类的属性 %s 上使用注解@CollectionView时，注解的name属性不能为空", entityClassName, field.getName()));
						}
						if (!Collection.class.isAssignableFrom(type)) {
							throw new IllegalArgumentException(format("%s 类上的属性 %s 上使用@CollectionView中注解时，注解type属性必须为Collection的实现类", entityClassName, field.getName()));
						}
						final Meta.CollectionField collectionField = new Meta.CollectionField();
						collectionField.name = name;
						collectionField.order = view.field().order();
						collectionField.label = handleFieldLabel(field, view.field().label(), format("%s 类中的字段 %s 上使用@AssociationView时,label属性为空,并且没有加@Field注解", entityClass.getName(), field.getName()));
						handleCollectionView(view, meta, type, collectionField, field.getType());
					}

				}
			}
		}
	}

	private void handleAssociationView(Class<?> entityClass, Map<String, Meta> result, String[] groups, Field field) {
		final AssociationView[] associationViews = field.getAnnotationsByType(AssociationView.class);
		if (associationViews.length > 0) {
			for (AssociationView view : associationViews) {
				String[] viewGroups = view.groups();
				if (viewGroups.length == 0) {
					viewGroups = groups.clone();
				}
				for (String group : viewGroups) {
					if (predicate.test(group)) {
						final Meta meta = metaCheck(entityClass, result, group);
						final String name = getName(field, view.name());
						final Meta.Field newField = new Meta.Field();
						if (isBlank(view.associationGroup())) {
							final Class<?> type = getType(field, view.type());
							newField.type = type.getSimpleName();
							addImport(meta, type);
						} else {
							newField.type = view.associationGroup();
						}
						newField.name = name;
						final String label = view.field().label();
						newField.order = view.field().order();
						newField.label = handleFieldLabel(field, label, format("%s 类中的字段 %s 上使用@AssociationView时,label属性为空,并且没有加@Field注解", entityClass.getName(), field.getName()));
						meta.getAssociationFields().add(newField);
					}

				}
			}
		}
	}

	private void handleFieldView(Class<?> entityClass, Map<String, Meta> result, String[] groups, Field field) {
		final View[] views = field.getAnnotationsByType(View.class);
		if (views.length > 0) {
			for (View view : views) {
				String[] viewGroups = view.groups();
				if (viewGroups.length == 0) {
					viewGroups = groups.clone();
				}
				for (String group : viewGroups) {
					if (predicate.test(group)) {
						final Meta meta = metaCheck(entityClass, result, group);
						final String name = getName(field, view.name());
						final Class<?> type = getType(field, view.type());
						final Meta.Field newField = new Meta.Field();
						newField.name = name;
						newField.type = type.getSimpleName();
						addImport(meta, type);
						final String label = view.field().label();
						newField.order = view.field().order();
						newField.label = handleFieldLabel(field, label, format("%s 类中的字段 %s 上使用@View时,@Field注解label属性为空,并且没有加@Field注解", entityClass.getName(), field.getName()));
						if (newField.name.equals(field.getName()) && newField.type.equals(field.getType().getSimpleName())) {
							meta.getFields().add(newField);
						} else {
							meta.getAssociationFields().add(newField);
						}

					}

				}
			}
		}
	}

	private String handleFieldLabel(Field field, String label, String format) {
		if (isBlank(label)) {
			if (field.isAnnotationPresent(io.gd.generator.annotation.Field.class)) {
				final io.gd.generator.annotation.Field fieldAnno = field.getDeclaredAnnotation(io.gd.generator.annotation.Field.class);
				return fieldAnno.label();
			} else {
				logger.warn(format + ",默认使用 " + field.getName());
				return field.getName();
			}
		} else {
			return label;
		}
	}

	private Class<?> getType(Field field, Class<?> type) {
		if (type == Object.class) {
			type = field.getType();
		}
		return type;
	}

	private String getName(Field field, String name) {
		if (isBlank(name)) {
			name = field.getName();
		}
		return name;
	}


	private void handleClassMapView(Class<?> entityClass, MapView[] mapViews, Map<String, Meta> result, String[] groups) {
		for (MapView view : mapViews) {
			String[] viewGroups = view.groups();
			if (viewGroups.length == 0) {
				viewGroups = groups.clone();
			}
			for (String group : viewGroups) {
				if (predicate.test(group)) {
					final String entityClassName = entityClass.getName();
					final String format = format("%s 类上的注解@MapView中的type属性必须为Map", entityClassName);
					String label = view.field().label();
					if (isBlank(label)) {
						label = view.name();
						logger.warn(format("%s 类上使用@MapView（name=%s）中的label属性为空，默认使用name属性的 [%s]", entityClass.getName(), view.name(), label));
					}
					doHandleMapView(entityClass, result, view, group, entityClassName, format, entityClass, label, view.field().order());

				}

			}
		}
	}

	private void doHandleMapView(Class<?> entityClass, Map<String, Meta> result, MapView view, String group, String entityClassName, String format, Class<?> simple, String label, int order) {
		final Meta meta = metaCheck(entityClass, result, group);
		final String name = checkName(entityClassName, view.name());
		final Class<?> type = view.type();

		if (!Map.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException(format);
		}
		final Meta.MapField field = new Meta.MapField();
		field.name = name;
		field._interface = Map.class.getSimpleName();
		addImport(meta, Map.class);
		field.type = type.getSimpleName();
		addImport(meta, type);
		if (isNotBlank(view.keyGroup())) {
			field.key = view.keyGroup();
		} else {
			field.key = view.keyType().getSimpleName();
			addImport(meta, view.keyType());
		}

		if (isNotBlank(view.valueGroup())) {
			field.value = view.valueGroup();
		} else {
			if (view.valueType() == Object.class) {
				field.value = simple.getSimpleName();
				addImport(meta, simple);
			} else {
				field.value = view.valueType().getSimpleName();
				addImport(meta, view.valueType());
			}
		}
		field.label = label;
		field.order = order;
		addImport(meta, type);
		meta.mapFields.add(field);
	}

	private void handleClassCollectionView(Class<?> entityClass, CollectionView[] collectionViews, Map<String, Meta> result, String[] groups) {
		for (CollectionView view : collectionViews) {
			String[] viewGroups = view.groups();
			if (viewGroups.length == 0) {
				viewGroups = groups.clone();
			}
			for (String group : viewGroups) {
				if (predicate.test(group)) {
					final Meta meta = metaCheck(entityClass, result, group);
					final String entityClassName = entityClass.getName();
					final String name = view.name();
					if (isBlank(name))
						throw new NullPointerException(format("%s 类上的@CollectionView注解的name属性必填", entityClass));
					final Class<?> type = view.type();
					if (!Collection.class.isAssignableFrom(type)) {
						throw new IllegalArgumentException(format("%s 类上的注解@CollectionView中的type属性必须为Collection的实现类", entityClassName));
					}
					final Meta.CollectionField field = new Meta.CollectionField();
					field.name = name;
					String label = view.field().label();
					if (isBlank(label)) {
						label = view.name();
						logger.warn(format("%s 类上使用@AssociationView（name=%s）中的label属性为空，默认使用name属性 [%s]", entityClass.getName(), label, name, label));
					}
					field.label = label;
					handleCollectionView(view, meta, type, field, entityClass);
				}


			}
		}
	}

	private void handleCollectionView(CollectionView view, Meta meta, Class<?> type, Meta.CollectionField field, Class<?> simple) {
		if (List.class.isAssignableFrom(type)) {
			field._interface = List.class.getSimpleName();
			addImport(meta, List.class);
		} else if (Set.class.isAssignableFrom(type)) {
			field._interface = Set.class.getSimpleName();
			addImport(meta, Set.class);
		} else {
			field._interface = Collection.class.getSimpleName();
			addImport(meta, Collection.class);
		}
		field.type = type.getSimpleName();
		addImport(meta, type);
		final String elementGroup = view.elementGroup();
		view.elementType();
		if (isNotBlank(elementGroup)) {
			field.elementGroup = elementGroup;
		} else {
			if (view.elementType() == Object.class) {
				field.elementGroup = simple.getSimpleName();
				addImport(meta, simple);
			} else {
				field.elementGroup = view.elementType().getSimpleName();
				addImport(meta, view.elementType());
			}
		}
		addImport(meta, type);
		field.order = view.field().order();
		meta.collectionFields.add(field);
	}

	private Map<String, Meta> initMeta(String[] groups) {
		Map<String, Meta> result = new HashMap<>();
		Arrays.stream(groups).forEach(voName -> {
			final Meta meta = new Meta();
			meta.className = voName;
			meta.voPackage = voPackage;
			meta.useLombok = useLombok;
			result.put(voName, meta);
		});
		return result;
	}

	private void handleClassAssociationView(Class<?> entityClass, AssociationView[] associationViews, Map<String, Meta> result, String[] groups) {
		for (AssociationView view : associationViews) {
			String[] viewGroups = view.groups();
			if (viewGroups.length == 0) {
				viewGroups = groups.clone();
			}
			for (String group : viewGroups) {
				if (predicate.test(group)) {
					metaCheck(entityClass, result, group);
					final Meta meta = metaCheck(entityClass, result, group);
					final String entityClassName = entityClass.getName();
					final String name = checkName(entityClassName, view.name());
					final Class<?> type = view.type();
					final String associationGroup = view.associationGroup();
					if ((type.getName().startsWith("java") || type.isPrimitive()) && isBlank(associationGroup)) {
						throw new IllegalArgumentException(format("%s 类上的注解@AssociationView的type属性,为java包的类或者基本类型,associationGroup属性必须填写", entityClass));
					}
					final Meta.Field field = new Meta.Field();
					field.name = name;
					if (isNotBlank(associationGroup)) {
						field.type = associationGroup;
					} else if (!type.getName().startsWith("java")) {
						field.type = type.getSimpleName();
						addImport(meta, type);
					}
					String label = view.field().label();
					if (isBlank(label)) {
						label = view.name();
						logger.warn(format("%s 类上使用@AssociationView（name=%s）中的label属性为空，默认使用name属性 [%s]", entityClass.getName(), name, label));
					}
					field.label = label;
					field.order = view.field().order();
					meta.associationFields.add(field);
				}

			}
		}
	}

	private void handleClassView(Class<?> entityClass, View[] views, Map<String, Meta> result, String[] groups) {
		for (View view : views) {
			String[] viewGroups = view.groups();
			if (viewGroups.length == 0) {
				viewGroups = groups.clone();
			}
			for (String viewGroup : viewGroups) {
				if (predicate.test(viewGroup)) {
					final Meta meta = metaCheck(entityClass, result, viewGroup);
					final String name = checkName(entityClass.getName(), view.name());
					final Class<?> type = checkType(entityClass.getName(), view.type());
					if (!type.getName().startsWith("java") && !type.isPrimitive())
						throw new IllegalArgumentException(format("%s 类在@View注解上错误的引用了非java提供的类,如果需要请使用@AssociationView", entityClass));
					final Meta.Field field = new Meta.Field();
					field.name = name;
					field.type = type.getSimpleName();
					addImport(meta, type);
					String label = view.field().label();
					if (isBlank(label)) {
						label = view.name();
						logger.warn(format("%s 类上使用@View（name=%s）中的label属性为空,默认使用name属性 [%s]", entityClass.getName(), name, name, viewGroup));
					}
					field.label = label;
					field.order = view.field().order();
					meta.associationFields.add(field);
				}

			}
		}
	}

	private String checkName(String entityClass, String name) {
		if (isBlank(name)) throw new NullPointerException(format("%s 类上的@View注解的name属性必填", entityClass));
		return name;
	}

	private Class<?> checkType(String entityClass, Class<?> type) {
		if (type == Object.class)
			throw new NullPointerException(format(" %s类上的@View注解的type属性必填", entityClass));
		return type;
	}

	private Meta metaCheck(Class<?> entityClass, Map<String, Meta> result, String viewGroup) {
		final Meta meta = result.get(viewGroup);
		if (meta == null)
			throw new NullPointerException(format("group %s 未在类 %s 上声明", viewGroup, entityClass.getName()));
		return meta;
	}

	private void addImport(Meta meta, Class<?> type) {
		if (!type.getName().startsWith("java.lang") && !type.isPrimitive()) {
			if (type.getName().contains("$")) {
				if (type.getName().startsWith("java")) {
					meta.importJava.add(type.getName().replace("$", "."));
				} else {
					meta.importOther.add(type.getName().replace("$", "."));
				}
			} else {
				if (type.getName().startsWith("java")) {
					meta.importJava.add(type.getName());
				} else {
					meta.importOther.add(type.getName());
				}

			}
		}
	}

	protected void doWrite(Map<String, Meta> groupClassMap) {
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
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		});


	}


	public static class Meta {
		private Set<String> importJava = new HashSet<>();
		private Set<String> importOther = new HashSet<>();
		private List<Field> fields = new ArrayList<>();
		private List<Field> associationFields = new ArrayList<>();
		private List<CollectionField> collectionFields = new ArrayList<>();
		private List<MapField> mapFields = new ArrayList<>();
		private String voPackage;
		private String className;
		private boolean useLombok;

		public List<Field> getFields() {
			return fields;
		}

		public void setFields(List<Field> fields) {
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

		public List<Field> getAssociationFields() {
			return associationFields;
		}

		public void setAssociationFields(List<Field> associationFields) {
			this.associationFields = associationFields;
		}

		public Set<String> getImportJava() {
			return importJava;
		}

		public void setImportJava(Set<String> importJava) {
			this.importJava = importJava;
		}

		public Set<String> getImportOther() {
			return importOther;
		}

		public void setImportOther(Set<String> importOther) {
			this.importOther = importOther;
		}

		public List<CollectionField> getCollectionFields() {
			return collectionFields;
		}

		public void setCollectionFields(List<CollectionField> collectionFields) {
			this.collectionFields = collectionFields;
		}

		public List<MapField> getMapFields() {
			return mapFields;
		}

		public void setMapFields(List<MapField> mapFields) {
			this.mapFields = mapFields;
		}

		public static class MapField extends Field {
			private String _interface;
			private String key;
			private String value;

			public String get_interface() {
				return _interface;
			}

			public void set_interface(String _interface) {
				this._interface = _interface;
			}

			public String getKey() {
				return key;
			}

			public void setKey(String key) {
				this.key = key;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}
		}

		public static class CollectionField extends Field {
			private String _interface;
			private String elementGroup;

			public String get_interface() {
				return _interface;
			}

			public void set_interface(String _interface) {
				this._interface = _interface;
			}


			public String getElementGroup() {
				if (elementGroup == null || "".equals(elementGroup)) return "";
				return "<" + elementGroup + ">";
			}

			public void setElementGroup(String elementGroup) {
				this.elementGroup = elementGroup;
			}
		}

		public static class Field {
			protected String name;
			protected String type;
			protected String label;
			protected int order;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}


			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public String getLabel() {
				return label;
			}

			public void setLabel(String label) {
				this.label = label;
			}

			public int getOrder() {
				return order;
			}

			public void setOrder(int order) {
				this.order = order;
			}
		}
	}


}


