package ru.javawebinar.topjava.ddl_script;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.repository.MetadataIntegratorProvider;
import ru.javawebinar.topjava.service.AbstractServiceTest;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import java.nio.file.FileSystems;
import java.util.EnumSet;
import java.util.Set;

@ContextConfiguration(value = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml",
})
@RunWith(SpringRunner.class)
@ActiveProfiles(resolver = ActiveDbProfileResolver.class, value = Profiles.REPOSITORY_IMPLEMENTATION)
public class SchemaGenerator extends AbstractServiceTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    MetadataIntegratorProvider integratorProvider;

    @Test
    public void isSqlScriptGeneratedSuccess() {
        System.out.println();
        String filename = FileSystems.getDefault().getPath("config/ddl", "generated.sql").toString();
//        generateDdlScript(filename, "ru.javawebinar.topjava.model");
        generateDdlScriptNew(filename);
    }

    private void generateDdlScriptNew(String filename) {
        Metadata metadata = ((MetadataIntegratorProvider) integratorProvider).getMetadata();
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setFormat(true).setOutputFile(filename).setOverrideOutputFileContent();
        schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.BOTH, metadata);
    }

    private void generateDdlScript(String filename, String entityPack) {
        SessionFactoryImplementor implementor = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        StandardServiceRegistry serviceRegistry = (StandardServiceRegistry) implementor
                .getServiceRegistry().getParentServiceRegistry();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        for (Class<?> clazz : getClasses(entityPack)) {
            metadataSources.addAnnotatedClass(clazz);
        }
        Metadata metadata = metadataSources.buildMetadata();
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setFormat(true).setOutputFile(filename).setOverrideOutputFileContent();
        schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.BOTH, metadata);
    }

    private static Set<Class<?>> getClasses(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(Entity.class);
    }
}
