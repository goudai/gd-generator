package io.gd.generator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ClassHelper {

    static final Logger logger = LoggerFactory.getLogger(ClassHelper.class);


    public static Set<Class<?>> getClasses(String pack) {
        return PackageUtil.getClasses(pack);

    }

    public static Map<String, Class<?>> getQuerysClasses(String queryModelPackage) {
        return getClasses(queryModelPackage).stream().collect(Collectors.toMap(Class::getSimpleName, k -> newIns(k.getSimpleName())));
    }

    public static Class<?> newIns(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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

    public static boolean isNotStaticField(Field field) {
        return !Modifier.isStatic(field.getModifiers());
    }

    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field[] declaredFields = clazz.getDeclaredFields();
                if (declaredFields != null)
                    fields.addAll(Arrays.asList(declaredFields));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return fields;
    }
}
