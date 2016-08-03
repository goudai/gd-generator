package io.gd.generator.handler;

import io.gd.generator.context.JdbcContext;
import io.gd.generator.context.GenLog;
import io.gd.generator.meta.mysql.MysqlColumnMeta;
import io.gd.generator.meta.mysql.MysqlTableMeta;
import io.gd.generator.util.ClassHelper;
import io.gd.generator.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MysqlHandler extends AbstractHandler<MysqlTableMeta, JdbcContext> {

	static final Logger logger = LoggerFactory.getLogger(MysqlHandler.class);

	@Override
	protected void preRead(JdbcContext context) throws Exception {
	}

	@Override
	protected MysqlTableMeta read(JdbcContext context) throws Exception {
		return null;
	}

	@Override
	protected MysqlTableMeta parse(JdbcContext context) throws Exception {
		Class<?> entityClass = context.getEntityClass();
		Table table = entityClass.getDeclaredAnnotation(Table.class);
		MysqlTableMeta mtm = new MysqlTableMeta();
		mtm.setTable(ClassHelper.resolveTableName(entityClass));
		mtm.setKlass(entityClass);
		UniqueConstraint[] uniqueConstraints = table.uniqueConstraints();
		Arrays.asList(uniqueConstraints).forEach((un) -> {
			String[] columnNames = un.columnNames();
			if (columnNames != null && columnNames.length > 0) {
				String string = Arrays.asList(columnNames).stream().reduce((p, n) -> p + "," + n).get();
				mtm.getUniques().add(string);
			}
		});
		Arrays.asList(entityClass.getDeclaredFields()).stream().filter(ClassHelper::withoutField).forEach((field) -> {
			mtm.getMysqlColumnMetas().add(parseColumn(field));
			Column column = field.getDeclaredAnnotation(Column.class);
			if (column != null)
				if (column.unique())
					mtm.getUniques().add(field.getName());
		});

		return mtm;
	}

	@Override
	protected MysqlTableMeta merge(MysqlTableMeta parsed, MysqlTableMeta read, JdbcContext context) throws Exception {
		return parsed;
	}

	@Override
	protected void write(MysqlTableMeta merged, JdbcContext context) throws Exception {
		GenLog genLog = context.getGenLog();
		Connection connection = context.getConnection();
		String table = merged.getTable();
		Class<?> entityClass = context.getEntityClass();
		try (Statement st = connection.createStatement(); ResultSet executeQuery = st.executeQuery("show tables like '" + table + "'")) {
			if (!executeQuery.next()) {
				Map<String, Object> model = new HashMap<>();
				model.put("mtm", merged);
				String sql = renderTemplate("mysql", model, context);
				st.executeUpdate(sql);
				genLog.info(sql);
				logger.info(sql);
			} else {
				DatabaseMetaData metaData = connection.getMetaData();
				String string = metaData.getURL().toString();
				String db = string.substring(string.lastIndexOf("/") + 1);

				for (MysqlColumnMeta cm : merged.getMysqlColumnMetas()) {
					String sql = "SELECT * FROM information_schema.columns WHERE table_schema='" + db + "' and table_name = '" + table
							+ "' AND column_name = '" + cm.getName() + "'";
					try (ResultSet rs = st.executeQuery(sql)) {
						if (!rs.next()) {
							String addColumn = "ALTER TABLE `" + table + "` ADD COLUMN `" + cm.getName() + "` " + cm.getType();
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
	protected void postWrite(JdbcContext context) throws Exception {
	}

	private MysqlColumnMeta parseColumn(Field field) {
		String type = getMysqlType(field);
		Column column = field.getDeclaredAnnotation(Column.class);
		String name;
		if (column != null)
			name = column.name();
		else
			name = field.getName();
		if (!StringUtils.isNotBlank(name))
			name = field.getName();
		MysqlColumnMeta mysqlColumnMeta = new MysqlColumnMeta();
		mysqlColumnMeta.setName(StringUtils.camelToUnderline(name));
		mysqlColumnMeta.setType(type);
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
			Id id = field.getDeclaredAnnotation(Id.class);
			if (id == null)
				return "int(20)";
			return "int(20) not null AUTO_INCREMENT PRIMARY KEY";
		}
		if (typeName.toUpperCase().contains("int".toUpperCase())) {
			Id id = field.getDeclaredAnnotation(Id.class);
			if (id == null)
				return "int(11)";
			return "int(11) not null AUTO_INCREMENT PRIMARY KEY";
		}

		if (typeName.toUpperCase().contains("string".toUpperCase())) {
			Column column = field.getDeclaredAnnotation(Column.class);
			if (column == null)
				return "varchar(255)";
			int length = column.length();
			return "varchar(" + length + ")";
		}

		if (field.getType().isEnum()) {
			Enumerated enumd = field.getDeclaredAnnotation(Enumerated.class);
			if (enumd != null) {
				EnumType value = enumd.value();
				if (value.equals(EnumType.ORDINAL))
					return "int(2)";
				return "varchar(2)";
			}
			return "int(2)";
		}
		if (field.getType().isAssignableFrom(BigDecimal.class)) {
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
				return "decimal(19,2)";
			} else {
				int precision = column.precision() == 0 ? 19 : column.precision();
				int scale = column.scale() == 0 ? 19 : column.scale();
				return "decimal(" + precision + "," + scale + ")";
			}
		}
		if (field.getType().isAssignableFrom(Double.class)) {
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
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
