package io.gd.generator.meta.jpa;

/**
 * @author jianglin
 */
public class JpaRepositoryMeta {
    private String repositoryPackage;
    private String entityName;
    private String idType;
    private String entitySimpleName;

    public String getRepositoryPackage() {
        return repositoryPackage;
    }

    public JpaRepositoryMeta setRepositoryPackage(String repositoryPackage) {
        this.repositoryPackage = repositoryPackage;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public JpaRepositoryMeta setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getIdType() {
        return idType;
    }

    public JpaRepositoryMeta setIdType(String idType) {
        this.idType = idType;
        return this;
    }

    public String getEntitySimpleName() {
        return entitySimpleName;
    }

    public JpaRepositoryMeta setEntitySimpleName(String entitySimpleName) {
        this.entitySimpleName = entitySimpleName;
        return this;
    }
}
