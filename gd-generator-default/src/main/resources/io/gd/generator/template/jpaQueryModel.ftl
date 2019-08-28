<#-- @ftlvariable name="meta" type="io.gd.generator.meta.jpa.JpaQueryModelMeta" -->
package ${meta.queryModelPackage};

import ${meta.entityType};
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Objects;

<#if meta.useLombok>
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
</#if>
<#if meta.importFullTypes??>
    <#list meta.importFullTypes as importFullType>
import ${importFullType};
    </#list>
</#if>

<#if meta.useLombok>
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
</#if>
public class ${meta.type} implements Specification<${meta.entityName}> {
<#if meta.queryModelFields??>
    <#list meta.queryModelFields as queryModelField>

    private ${queryModelField.type}<#if queryModelField.array>[]</#if> ${queryModelField.name};
    </#list>
</#if>

<#if !meta.useLombok>
    <#if meta.queryModelFields??>
        <#list meta.queryModelFields as queryModelField>

    public ${queryModelField.type}<#if queryModelField.array>[]</#if> get${queryModelField.name?cap_first}() {
        return ${queryModelField.name};
    }

    public void set${queryModelField.name?cap_first}(${queryModelField.type}<#if queryModelField.array>[]</#if> ${queryModelField.name}) {
        this.${queryModelField.name} = ${queryModelField.name};
    }
        </#list>
    </#if>
</#if>

    @Override
    public Predicate toPredicate(Root<${meta.entityName}> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        Predicate defaultPredicate = criteriaBuilder.and();
<#if meta.queryModelFields??>
    <#list meta.queryModelFields as queryModelField>
        <#if queryModelField.array>
        if (Objects.nonNull(get${queryModelField.name?cap_first}())
                && get${queryModelField.name?cap_first}().length > 0) {
        <#else>
        if (Objects.nonNull(get${queryModelField.name?cap_first}())) {
        </#if>
    <#switch queryModelField.predicate>
    <#case "EQ">
            Predicate p = criteriaBuilder.equal(root.get("${queryModelField.fieldName}"), get${queryModelField.name?cap_first}());
        <#break>
    <#case "NEQ">
            Predicate p = criteriaBuilder.notEqual(root.get("${queryModelField.fieldName}"), get${queryModelField.name?cap_first}());
        <#break>
    <#case "GT">
            Predicate p = criteriaBuilder.greaterThan(root.get("${queryModelField.fieldName}"), get${queryModelField.name?cap_first}());
        <#break>
    <#case "GTE">
            Predicate p = criteriaBuilder.greaterThanOrEqualTo(root.get("${queryModelField.fieldName}"), get${queryModelField.name?cap_first}());
        <#break>
    <#case "LT">
            Predicate p = criteriaBuilder.lessThan(root.get("${queryModelField.fieldName}"), get${queryModelField.name?cap_first}());
        <#break>
    <#case "LTE">
            Predicate p = criteriaBuilder.lessThanOrEqualTo(root.get("${queryModelField.fieldName}"), get${queryModelField.name?cap_first}());
        <#break>
    <#case "NL">
            Predicate p = criteriaBuilder.isNull(root.get("${queryModelField.fieldName}"));
        <#break>
    <#case "NN">
            Predicate p = criteriaBuilder.isNotNull(root.get("${queryModelField.fieldName}"));
        <#break>
    <#case "LK">
            Predicate p = criteriaBuilder.like(root.get("${queryModelField.fieldName}"), "%" + get${queryModelField.name?cap_first}() + "%");
        <#break>
    <#case "SW">
            Predicate p = criteriaBuilder.like(root.get("${queryModelField.fieldName}"), get${queryModelField.name?cap_first}() + "%");
        <#break>
    <#case "EW">
            Predicate p = criteriaBuilder.like(root.get("${queryModelField.fieldName}"), "%" + get${queryModelField.name?cap_first}());
        <#break>
    <#case "IN">
            Predicate p = criteriaBuilder.and(root.get("${queryModelField.fieldName}").in(java.util.Arrays.asList(get${queryModelField.name?cap_first}())));
        <#break>
    </#switch>
            defaultPredicate = criteriaBuilder.and(defaultPredicate, p);
        }
    </#list>
</#if>
        return defaultPredicate;
    }
}