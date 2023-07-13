package ru.javawebinar.topjava.service;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.Test;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import java.nio.file.FileSystems;
import java.util.EnumSet;
import java.util.Set;

@ActiveProfiles("datajpa")
public class SchemaGenerator extends AbstractServiceTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Test
    public void generateFileScriptSql() {
        System.out.println();
        generateDdlScript(FileSystems.getDefault().getPath("config/ddl", "generated.sql").toString(),
                "ru.javawebinar.topjava.model");
    }

    private void generateDdlScript(String filename, String entityPack) {
        try (SessionFactoryImplementor implementor = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
             StandardServiceRegistry serviceRegistry = (StandardServiceRegistry) implementor
                     .getServiceRegistry().getParentServiceRegistry()) {

            MetadataSources metadataSources = new MetadataSources(serviceRegistry);
            for (Class<?> clazz : getClasses(entityPack)) {
                metadataSources.addAnnotatedClass(clazz);
            }
            Metadata metadata = metadataSources.buildMetadata();
            SchemaExport schemaExport = new SchemaExport();
            schemaExport.setFormat(true).setOutputFile(filename).setOverrideOutputFileContent();
            schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.BOTH, metadata);
        }
    }

    private static Set<Class<?>> getClasses(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(Entity.class);
    }
}
