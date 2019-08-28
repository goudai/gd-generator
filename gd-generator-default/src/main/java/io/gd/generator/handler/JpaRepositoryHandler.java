package io.gd.generator.handler;

import io.gd.generator.meta.jpa.JpaRepositoryMeta;
import io.gd.generator.util.ConfigChecker;

import javax.persistence.Id;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jianglin
 * @date 2019-08-28
 */
public class JpaRepositoryHandler extends ScopedHandler<JpaRepositoryMeta> {

    private boolean overwrite;

    public JpaRepositoryHandler(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public JpaRepositoryHandler() {
        this(false);
    }

    private String getRepositoryFilePath(Class<?> entityClass) {
        return config.getJpaRepositoryPath() + File.separator + entityClass.getSimpleName() + "Repository.java";
    }

    @Override
    protected void init() throws Exception {
        super.init();
        ConfigChecker.notBlank(config.getJpaRepositoryPackage(), "config jpaRepositoryPackage is miss");
        ConfigChecker.notBlank(config.getJpaRepositoryPath(), "config jpaRepositoryPath is miss");

        String jpaRepositoryPath = config.getJpaRepositoryPath();

        /* 初始化文件夹 */
        File jpaRepositoryPathDir = new File(jpaRepositoryPath);
        if (!jpaRepositoryPathDir.exists()) {
            jpaRepositoryPathDir.mkdirs();
        } else if (!jpaRepositoryPathDir.isDirectory()) {
            throw new IllegalArgumentException("jpaRepositoryPath is not a directory");
        }

    }

    @Override
    protected void preRead(Class<?> entityClass) throws Exception {

    }

    @Override
    protected JpaRepositoryMeta read(Class<?> entityClass) throws Exception {
        return null;
    }

    @Override
    protected JpaRepositoryMeta parse(Class<?> entityClass) throws Exception {
        JpaRepositoryMeta meta = new JpaRepositoryMeta();
        meta.setEntityName(entityClass.getName());
        meta.setEntitySimpleName(entityClass.getSimpleName());
        meta.setRepositoryPackage(config.getJpaRepositoryPackage());
        return meta;
    }

    @Override
    protected JpaRepositoryMeta merge(JpaRepositoryMeta parsed, JpaRepositoryMeta read, Class<?> entityClass) throws Exception {


        return parsed;
    }

    @Override
    protected void write(JpaRepositoryMeta merged, Class<?> entityClass) throws Exception {

        Map<String, Object> model = new HashMap<>();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Id declaredAnnotation = field.getDeclaredAnnotation(Id.class);
            if (declaredAnnotation != null) {
                merged.setIdType(field.getType().getSimpleName());
                break;
            }
        }
        model.put("meta", merged);
        String mapper = renderTemplate("jpaRepository", model);
        File file = new File(getRepositoryFilePath(entityClass));

        if (file.exists()) {
            if (!overwrite) {
                return;
            }
            file.delete();
            logger.info("overwrite {}", file.getName());
        }
        file.createNewFile();
        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(mapper.getBytes());
        }
    }

    @Override
    protected void postWrite(Class<?> entityClass) throws Exception {

    }
}
