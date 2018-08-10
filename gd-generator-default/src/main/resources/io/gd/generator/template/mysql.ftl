CREATE TABLE `${meta.table?trim}` (
	<#list meta.mysqlColumnMetas as mysqlColumnMeta>
	`${mysqlColumnMeta.name}` ${mysqlColumnMeta.type}<#if mysqlColumnMeta_has_next>,</#if>
	</#list>
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;