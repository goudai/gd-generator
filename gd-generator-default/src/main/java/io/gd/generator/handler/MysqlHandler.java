package io.gd.generator.handler;

import io.gd.generator.meta.mysql.MysqlTableMeta;
import io.gd.generator.meta.mysql.MysqlTableMeta.MysqlColumnMeta;
import io.gd.generator.util.ClassHelper;
import io.gd.generator.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MysqlHandler extends ScopedHandler<MysqlTableMeta> {

    static final Logger logger = LoggerFactory.getLogger(MysqlHandler.class);

    protected Connection connection;

    private boolean useGeneratedKeys = true;

    public MysqlHandler() {
        this.useGeneratedKeys = true;
    }

    public MysqlHandler(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    @Override
    protected void init() throws Exception {
        super.init();
        connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
    }

    @Override
    protected void destroy() throws Exception {
        super.destroy();
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    protected void preRead(Class<?> entityClass) throws Exception {
    }

    @Override
    protected MysqlTableMeta read(Class<?> entityClass) throws Exception {
        return null;
    }

    @Override
    protected MysqlTableMeta parse(Class<?> entityClass) throws Exception {
        Table table = entityClass.getDeclaredAnnotation(Table.class);
        MysqlTableMeta mtm = new MysqlTableMeta();
        mtm.setTable(ClassHelper.resolveTableName(entityClass));
        mtm.setKlass(entityClass);

        io.gd.generator.annotation.Type type = entityClass.getDeclaredAnnotation(io.gd.generator.annotation.Type.class);
        if (type != null) {
            mtm.setComment(type.label());
        }
        if (StringUtils.isBlank(mtm.getComment())) {
            mtm.setComment(table.schema());
        }
        UniqueConstraint[] uniqueConstraints = table.uniqueConstraints();
        Arrays.asList(uniqueConstraints).forEach((un) -> {
            String[] columnNames = un.columnNames();
            if (columnNames != null && columnNames.length > 0) {
                String string = Arrays.asList(columnNames).stream().reduce((p, n) -> p + "," + n).get();
                mtm.getUniques().add(string);
            }
        });
        Arrays.asList(entityClass.getDeclaredFields()).stream().filter(ClassHelper::isNotStaticField).forEach((field) -> {
            mtm.getMysqlColumnMetas().add(parseColumn(field));
            Column column = field.getDeclaredAnnotation(Column.class);
            if (column != null)
                if (column.unique())
                    mtm.getUniques().add(field.getName());
        });

        return mtm;
    }

    @Override
    protected MysqlTableMeta merge(MysqlTableMeta parsed, MysqlTableMeta read, Class<?> entityClass) throws Exception {
        return parsed;
    }

    @Override
    protected void write(MysqlTableMeta merged, Class<?> entityClass) throws Exception {
        String table = merged.getTable();
        try (Statement st = connection.createStatement(); ResultSet executeQuery = st.executeQuery("show tables like '" + table + "'")) {
            if (!executeQuery.next()) {
                Map<String, Object> model = new HashMap<>();
                model.put("meta", merged);
                String sql = renderTemplate("mysql", model);
                st.executeUpdate(sql);
                genLog.info(sql);
                logger.info(sql);
            } else {
                DatabaseMetaData metaData = connection.getMetaData();
                String string = metaData.getURL().toString();
                String db = string.substring(string.lastIndexOf("/") + 1);
                db = db.substring(0, db.indexOf("?"));

                for (MysqlColumnMeta cm : merged.getMysqlColumnMetas()) {
                    String sql = "SELECT * FROM information_schema.columns WHERE table_schema='" + db + "' and table_name = '" + table
                            + "' AND column_name = '" + cm.getName() + "'";
                    try (ResultSet rs = st.executeQuery(sql)) {
                        if (!rs.next()) {
                            String addColumn = "ALTER TABLE `" + table + "` ADD COLUMN `" + cm.getName() + "` " + cm.getType();

                            if (StringUtils.isNotBlank(cm.getComment())) {
                                addColumn += " COMMENT '" + cm.getComment() + "'";
                            }

                            st.executeUpdate(addColumn);
                            genLog.info(addColumn);
                            logger.info(addColumn);
                        }
                    }
                }

            }
        }

        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet rs = metaData.getColumns(null, "%", table, "%");) {
            while (rs.next()) {
                String string = rs.getString(4);
                String field = StringUtils.underlineToCamel(string);
                try {
                    entityClass.getDeclaredField(field);
                } catch (NoSuchFieldException e) {
                    String message = "数据库中的列  [" + string + " --> " + field + " ]" + "在实体类 " + entityClass.getSimpleName() + " 不存在";
                    genLog.warn(message);
                    logger.warn(message);
                }
            }
        }

        try (Statement createStatement = connection.createStatement()) {
            for (String un : merged.getUniques()) {
                try {
                    if (!un.contains(",")) {
                        String sql = "ALTER TABLE `" + table + "` ADD UNIQUE unique_" + StringUtils.camelToUnderline(un) + "("
                                + StringUtils.camelToUnderline(un) + ");";
                        createStatement.executeUpdate(sql);
                        genLog.info(sql);
                        logger.info(sql);
                    } else {
                        String uniqueName = Arrays.asList(un.split(",")).stream()
                                .reduce((p, n) -> StringUtils.camelToUnderline(p) + "_" + StringUtils.camelToUnderline(n)).get();
                        String unique = Arrays.asList(un.split(",")).stream()
                                .reduce((p, n) -> StringUtils.camelToUnderline(p) + "," + StringUtils.camelToUnderline(n)).get();
                        String sql = "ALTER TABLE `" + table + "` ADD UNIQUE unique_" + uniqueName + "(" + unique + ");";
                        createStatement.executeUpdate(sql);
                        genLog.info(sql);
                        logger.info(sql);
                    }
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (!message.startsWith("Duplicate")) {
                        throw e;
                    }
                }
            }
        }

    }

    @Override
    protected void postWrite(Class<?> entityClass) throws Exception {
    }

    private MysqlColumnMeta parseColumn(Field field) {
        String type = getMysqlType(field);
        Column column = field.getDeclaredAnnotation(Column.class);
        String name, label = null;
        if (column != null) {
            name = StringUtils.isBlank(column.name()) ? field.getName() : column.name();
        } else {
            name = field.getName();
        }

        final io.gd.generator.annotation.Field fieldAnno = field.getDeclaredAnnotation(io.gd.generator.annotation.Field.class);

        if (fieldAnno != null) {
            label = fieldAnno.label();
        }

        MysqlColumnMeta mysqlColumnMeta = new MysqlColumnMeta();
        mysqlColumnMeta.setName(StringUtils.camelToUnderline(name));
        mysqlColumnMeta.setType(type);
        mysqlColumnMeta.setComment(label);
        return mysqlColumnMeta;
    }

    private String getMysqlType(Field field) {
        Type genericType = field.getType();
        String typeName = genericType.getTypeName();
        Lob lob = field.getDeclaredAnnotation(Lob.class);
        if (lob != null) {
            if (field.getType().isAssignableFrom(String.class))
                return "longtext";
            return "blob";
        }

        if (typeName.toUpperCase().contains("boolean".toUpperCase()))
            return "bit(1)";

        if (typeName.toUpperCase().contains("Date".toUpperCase())) {
            Temporal dateType = field.getDeclaredAnnotation(Temporal.class);
            if (dateType != null) {
                TemporalType value = dateType.value();
                if (value != null)
                    if (value.equals(TemporalType.DATE))
                        return "date";
                    else if (value.equals(TemporalType.TIMESTAMP))
                        return "datetime";
                    else if (value.equals(TemporalType.TIME))
                        return "time";
            }
            return "datetime";
        }
        if (typeName.toUpperCase().contains("long".toUpperCase())) {
            Column column = field.getDeclaredAnnotation(Column.class);
            int length = 32;
            if (column != null) {
                final int length1 = column.length();
                if (length1 != 255 && length1 > 0 && length1 < 255) {
                    length = length1;
                }
            }
            Id id = field.getDeclaredAnnotation(Id.class);
            if (id == null)
                return "bigInt(" + length + ")";
            if (useGeneratedKeys)
                return "bigInt(" + length + ") not null AUTO_INCREMENT PRIMARY KEY";
            return "bigInt(" + length + ") not null PRIMARY KEY";
        }
        if (typeName.toUpperCase().contains("int".toUpperCase())) {
            Id id = field.getDeclaredAnnotation(Id.class);
            if (id == null)
                return "int(11)";
            if (useGeneratedKeys)
                return "int(11) not null AUTO_INCREMENT PRIMARY KEY";
            return "int(11) not null  PRIMARY KEY";
        }

        if (typeName.toUpperCase().contains("string".toUpperCase())) {
            Id id = field.getDeclaredAnnotation(Id.class);
            if (id == null) {
                Column column = field.getDeclaredAnnotation(Column.class);
                if (column == null)
                    return "varchar(255)";
                else {
                    final String columnDefinition = column.columnDefinition();
                    if (columnDefinition == null || "".equals(columnDefinition)) {
                        int length = column.length();
                        return "varchar(" + length + ")";
                    }
                    return columnDefinition;
                }
            }
            if (useGeneratedKeys)
                return "BIGINT(20) not null AUTO_INCREMENT PRIMARY KEY";
            return "BIGINT(20) not null  PRIMARY KEY";
        }

        if (field.getType().isEnum()) {
            Enumerated enumd = field.getDeclaredAnnotation(Enumerated.class);
            Column column = field.getDeclaredAnnotation(Column.class);
            int length = 255;
            if(column!= null && column.length() > 0){
                length = column.length();
            }
            if (enumd != null) {
                EnumType value = enumd.value();
                if (value.equals(EnumType.ORDINAL))
                    return "int(2)";
                return "varchar("+length+")";
            }
            return "int(2)";
        }
        if (field.getType().isAssignableFrom(BigDecimal.class)) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                return "decimal(19,2)";
            } else {
                int precision = column.precision() == 0 ? 19 : column.precision();
                int scale = column.scale() == 0 ? 19 : column.scale();
                return "decimal(" + precision + "," + scale + ")";
            }
        }
        if (field.getType().isAssignableFrom(Double.class)) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                return "double(19,2)";
            } else {
                int precision = column.precision() == 0 ? 19 : column.precision();
                int scale = column.scale() == 0 ? 19 : column.scale();
                return "double(" + precision + "," + scale + ")";
            }
        }

        throw new RuntimeException(typeName + " 无法解析。请检查getMysqlType解析方法");
    }

}
