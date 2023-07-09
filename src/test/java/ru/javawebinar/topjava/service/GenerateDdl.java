package ru.javawebinar.topjava.service;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import javax.persistence.EntityManagerFactory;
import java.nio.file.FileSystems;
import java.util.EnumSet;

@ActiveProfiles("jpa")
public class GenerateDdl extends AbstractServiceTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    public <T> void generateDdl(String filename, Class<T>... clazz) {
        try (SessionFactoryImplementor implementor = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
             StandardServiceRegistry serviceRegistry = (StandardServiceRegistry) implementor
                     .getServiceRegistry().getParentServiceRegistry()) {
            MetadataSources metadataSources = new MetadataSources(serviceRegistry);
            for (Class<T> current : clazz) {
                metadataSources.addAnnotatedClass(current);
            }
            Metadata metadata = metadataSources.buildMetadata();
            SchemaExport schemaExport = new SchemaExport();
            schemaExport.setFormat(true).setOutputFile(filename).setOverrideOutputFileContent();
            schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.BOTH, metadata);
        }
    }

    @Test
    public void generate() {
        System.out.println();
        generateDdl(FileSystems.getDefault().getPath("config/ddl", "generated.sql").toString(), new Class[]{User.class, Meal.class});
    }
}
