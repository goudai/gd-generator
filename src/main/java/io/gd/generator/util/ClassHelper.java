package io.gd.generator.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ClassHelper {

	static final Logger logger = LoggerFactory.getLogger(ClassHelper.class);

	static ClassPath classpath;

	static {
		try {
			classpath = ClassPath.from(ClassHelper.class.getClassLoader());

		} catch (IOException e) {
			logger.error("create classpath error", e);
		}
	}

	public static Set<Class<?>> getClasses(String pack) {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		ImmutableSet<ClassInfo> topLevelClassesRecursive = classpath.getTopLevelClassesRecursive(pack);
		topLevelClassesRecursive.forEach(k -> {
			try {
				classes.add(Class.forName(k.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return classes;

	}

	public static Map<String, Class<?>> getQuerysClasses(String queryModelPackage) {
		Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		classpath.getTopLevelClassesRecursive(queryModelPackage).forEach(k -> {
			try {
				classes.put(k.getSimpleName(), Class.forName(k.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return classes;
	}

	public static String resolveTableName(Class<?> entityClass) {
		Table table = entityClass.getDeclaredAnnotation(Table.class);
		if (table == null) {
			throw new IllegalArgumentException("@Table注解缺失");
		}
		if (StringUtils.isNotBlank(table.name())) {
			return table.name();
		} else {
			return StringUtils.camelToUnderline(entityClass.getSimpleName());
		}
	}

	public static boolean withoutField(Field field) {
		return !(field.getName().equals("serialVersionUID")) && !Modifier.isStatic(field.getModifiers());
	}

	public static java.util.List<Field> getFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		for (; clazz!=null && clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				Field[] declaredFields = clazz.getDeclaredFields();
				if(declaredFields != null)
				fields.addAll(Arrays.asList(declaredFields));
			} catch (Exception e) {
				//ig
			}
		}
		return fields;
	}
}
