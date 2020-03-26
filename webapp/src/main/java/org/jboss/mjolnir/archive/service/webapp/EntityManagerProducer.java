package org.jboss.mjolnir.archive.service.webapp;

import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerProducer {

    /**
     * Make datasource available via CDI.
     *
     * TODO: The datasource is only required by ConfigurationProducer, make it use EntityManager instead.
     */
    @Produces @ApplicationScoped
    DataSource createDataSource() throws NamingException {
        InitialContext initialContext = new InitialContext();
        return (DataSource) initialContext.lookup(Constants.DATASOURCE_JNDI_NAME);
    }


    @Produces @ApplicationScoped
    EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.datasource", Constants.DATASOURCE_JNDI_NAME);

        return new HibernatePersistenceProvider().createEntityManagerFactory("mjolnir-archive-service", properties);
    }

    /**
     * Make EntityManager available via CDI.
     *
     * Since we use RESOURCE_LOCAL transactions, an entity manager must be created manually. It must not be injected via
     * {@code @PersistenceContext} annotation.
     */
    @Produces @RequestScoped
    EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

}
