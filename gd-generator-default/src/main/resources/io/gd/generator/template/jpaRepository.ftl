<#-- @ftlvariable name="meta" type="io.gd.generator.meta.jpa.JpaRepositoryMeta" -->
package ${meta.repositoryPackage};

import ${meta.entityName};
<#list meta.importFullTypes as type>
import ${type};
</#list>

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ${meta.entitySimpleName}Repository extends JpaRepository<${meta.entitySimpleName}, ${meta.idType}>, JpaSpecificationExecutor<${meta.entitySimpleName}> {

}
