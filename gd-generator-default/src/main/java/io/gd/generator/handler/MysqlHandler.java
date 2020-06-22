package io.gd.generator.handler;

import io.gd.generator.annotation.Default;
import io.gd.generator.meta.mysql.MysqlTableMeta;
import io.gd.generator.meta.mysql.MysqlTableMeta.MysqlColumnMeta;
import io.gd.generator.util.ClassHelper;
import io.gd.generator.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    private static String SPACE = " ";

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
                if(db.contains("?")){
                    db = db.substring(0, db.indexOf("?"));
                }
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
        Column column = field.getDeclaredAnnotation(Column.class);
        Default aDefault = field.getDeclaredAnnotation(Default.class);

        String notnull = "", primarykey = "", defaultstr = "";

        if (aDefault != null) {
            notnull = "NOT NULL";

            String defalutVal = "'" + aDefault.value() + "'";

            //关键字不加引号
            if (aDefault.type() == Default.DefaultType.DBKEY) {
                defalutVal = aDefault.value();
            }

            defaultstr = "DEFAULT " + defalutVal;
        }

        if (field.getDeclaredAnnotation(NotNull.class) != null
                || field.getDeclaredAnnotation(NotBlank.class) != null
                || field.getDeclaredAnnotation(NotEmpty.class) != null) {
            notnull = "NOT NULL";
        }

        if (column != null) {
            String columnDefinition = column.columnDefinition();

            if (!column.nullable()) {
                notnull = "NOT NULL";
            }

            if (StringUtils.isNotBlank(columnDefinition)) {
                if (!columnDefinition.toUpperCase().contains(notnull)) {
                    columnDefinition += SPACE + notnull;
                }
                if (!columnDefinition.toUpperCase().contains(defaultstr)) {
                    columnDefinition += SPACE + defaultstr;
                }

                return columnDefinition;
            }
        }

        Id id = field.getDeclaredAnnotation(Id.class);

        if (id != null) {
            primarykey = "PRIMARY KEY";

            if (useGeneratedKeys) {
                primarykey = "AUTO_INCREMENT" + SPACE + primarykey;
            }
        }

        Type genericType = field.getType();
        String typeName = genericType.getTypeName().toLowerCase();

        String columntype = "";

        if (field.getDeclaredAnnotation(Lob.class) != null) {
            columntype = "blob";

            if (field.getType().isAssignableFrom(String.class)) {
                columntype = "longtext";
            }
        }else if (typeName.contains("boolean")) {
            columntype = "bit(1)";
        }else if (typeName.contains("date")) {
            columntype = "datetime";

            Temporal dateType = field.getDeclaredAnnotation(Temporal.class);
            if (dateType != null) {
                TemporalType value = dateType.value();
                if (value != null) {
                    columntype = value.name().toLowerCase();
                }
            }
        } else if (typeName.contains("long")) {
            int length = 32;
            if (column != null) {
                if (column.length() != 255 && column.length() > 0 && column.length() < 255) {
                    length = column.length();
                }
            }
            columntype = "bigInt(" + length + ")";
        }else if (typeName.contains("int")) {
            columntype = "int(11)";
        }else if (typeName.contains("string")) {
            if (id == null) {
                if (column == null) {
                    columntype = "varchar(255)";
                } else {
                    int length = column.length();
                    columntype = "varchar(" + length + ")";
                }
            }else{
                columntype="BIGINT(20)";
            }
        }else if (field.getType().isEnum()) {
            Enumerated enumd = field.getDeclaredAnnotation(Enumerated.class);
            int length = 255;
            if (column != null && column.length() > 0) {
                length = column.length();
            }
            if (enumd != null) {
                EnumType value = enumd.value();
                if (value.equals(EnumType.ORDINAL)) {
                    columntype = "int(2)";
                }
                columntype = "varchar(" + length + ")";
            }
            columntype = "int(2)";
        } else if (field.getType().isAssignableFrom(BigDecimal.class)) {
            if (column == null) {
                columntype = "decimal(19,2)";
            } else {
                int precision = column.precision() == 0 ? 19 : column.precision();
                int scale = column.scale() == 0 ? 19 : column.scale();
                columntype = "decimal(" + precision + "," + scale + ")";
            }
        }else if (field.getType().isAssignableFrom(Float.class)) {
            if (column == null) {
                columntype = "float(9,2)";
            } else {
                int precision = column.precision() == 0 ? 9 : column.precision();
                int scale = column.scale() == 0 ? 9 : column.scale();
                columntype = "float(" + precision + "," + scale + ")";
            }
        } else if (field.getType().isAssignableFrom(Double.class)) {
            if (column == null) {
                columntype = "double(19,2)";
            } else {
                int precision = column.precision() == 0 ? 19 : column.precision();
                int scale = column.scale() == 0 ? 19 : column.scale();
                columntype = "double(" + precision + "," + scale + ")";
            }
        }

        if (StringUtils.isNotBlank(columntype)) {
            return columntype + SPACE + notnull + SPACE + defaultstr + SPACE + primarykey;
        }

        throw new RuntimeException(typeName + " 无法解析。请检查getMysqlType解析方法");
    }

}
