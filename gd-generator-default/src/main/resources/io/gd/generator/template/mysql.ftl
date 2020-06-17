CREATE TABLE `${meta.table?trim}` (
	<#list meta.mysqlColumnMetas as mysqlColumnMeta>
	`${mysqlColumnMeta.name}` ${mysqlColumnMeta.type} <#if mysqlColumnMeta.comment??> COMMENT '${mysqlColumnMeta.comment}'</#if><#if mysqlColumnMeta_has_next>,</#if>
	</#list>
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 <#if meta.comment??>COMMENT='${meta.comment}'</#if>;