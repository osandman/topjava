package ru.javawebinar.topjava.ddl_script;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javawebinar.topjava.repository.MetadataIntegratorProvider;

@Configuration
public class ConfigMetadata {
    @Bean
    public MetadataIntegratorProvider getMetadataProvider() {
        return new MetadataIntegratorProvider();
    }
}
