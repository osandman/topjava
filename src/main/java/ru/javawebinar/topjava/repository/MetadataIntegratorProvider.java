package ru.javawebinar.topjava.repository;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.util.Collections;
import java.util.List;

public class MetadataIntegratorProvider implements IntegratorProvider {
    private Metadata metadata;

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public List<Integrator> getIntegrators() {
        return Collections.singletonList(new Integrator() {
            @Override
            public void integrate(Metadata m, SessionFactoryImplementor sessionFactory,
                                  SessionFactoryServiceRegistry serviceRegistry) {
                metadata = m;
            }

            @Override
            public void disintegrate(SessionFactoryImplementor sessionFactory,
                                     SessionFactoryServiceRegistry serviceRegistry) {
            }
        });
    }
}
