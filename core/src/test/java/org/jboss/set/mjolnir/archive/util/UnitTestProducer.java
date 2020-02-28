package org.jboss.set.mjolnir.archive.util;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.jboss.set.mjolnir.archive.configuration.Configuration;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Produces beans for unit testing purposes.
 */
@Alternative
public class UnitTestProducer {

    @Produces
    @PersistenceContext
    @Singleton
    public EntityManager createEntityManager() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
        properties.put("hibernate.show_sql", "true");
        return Persistence.createEntityManagerFactory("mjolnir-archive-service", properties).createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }

    @Produces
    public Configuration createConfiguration() throws URISyntaxException {
        return new Configuration.ConfigurationBuilder()
                .setGitHubToken("")
                .setGitHubApiUri(new URI("http://localhost:8089"))
                .build();
    }

    @Produces
    public GitHubClient createGitHubClient(Configuration configuration) {
        return new GitHubClient(configuration.getGitHubApiHost(),
                configuration.getGitHubApiPort(),
                configuration.getGitHubApiScheme());
    }

}
