package org.jboss.mjolnir.archive.service.webapp;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerProducer {

    private final Logger logger = Logger.getLogger(getClass().getName());

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

        EntityManagerFactory emf = new HibernatePersistenceProvider().createEntityManagerFactory("mjolnir-archive-service", properties);
        logger.debugf("Created emf %s", emf.toString());
        return emf;
    }

    /**
     * Make EntityManager available via CDI.
     *
     * Since we use RESOURCE_LOCAL transactions, an entity manager must be created manually. It must not be injected via
     * {@code @PersistenceContext} annotation.
     */
    @Produces @RequestScoped
    EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
        EntityManager em = entityManagerFactory.createEntityManager();
        logger.debugf("Created em %s", em.toString());
        return em;
    }

    @SuppressWarnings("unused")
    public void closeEntityManagerFactory(@Disposes EntityManagerFactory emf) {
        logger.debugf("Closing emf %s", emf.toString());
        if (emf.isOpen()) {
            emf.close();
        }
    }

    @SuppressWarnings("unused")
    public void closeEntityManager(@Disposes EntityManager em) {
        logger.debugf("Closing em %s", em.toString());
        if (em.isOpen()) {
            em.close();
        }
    }

}
