CREATE TABLE `${mtm.table?trim}` (
	<#list mtm.mysqlColumnMetas as mysqlColumnMeta>
	`${mysqlColumnMeta.name}` ${mysqlColumnMeta.type}<#if mysqlColumnMeta_has_next>,</#if>
	</#list>
) ENGINE=InnoDB;