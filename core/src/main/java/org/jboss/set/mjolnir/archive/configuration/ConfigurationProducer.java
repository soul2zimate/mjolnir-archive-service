package org.jboss.set.mjolnir.archive.configuration;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jboss.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigurationProducer {

    private final static String GITHUB_TOKEN_KEY = "github.token";
    private final static String REPOSITORY_ARCHIVE_ROOT_KEY = "repository.archive.root";

    private Logger logger = Logger.getLogger(getClass());

    @Resource(lookup = "java:/app/datasource")
    private DataSource dataSource;

    @Produces
    public Configuration createConfiguration() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select param_name, param_value from application_parameters");
            ResultSet resultSet = stmt.executeQuery();

            Configuration.ConfigurationBuilder configurationBuilder = new Configuration.ConfigurationBuilder();

            while (resultSet.next()) {
                String name = resultSet.getString("param_name");
                String value = resultSet.getString("param_value");
                switch (name) {
                    case GITHUB_TOKEN_KEY:
                        configurationBuilder.setGitHubToken(value);
                        break;
                    case REPOSITORY_ARCHIVE_ROOT_KEY:
                        configurationBuilder.setRepositoryArchiveRoot(value);
                        break;
                    default:
                        logger.infof("Skipping configuration parameter %s", name);
                }
            }

            return configurationBuilder.build();
        } catch (SQLException e) {
            throw new RuntimeException("Can't connect to database", e);
        }
    }

    @Produces
    public GitHubClient createGitHubClient(Configuration configuration) {
        GitHubClient gitHubClient = new GitHubClient();
        gitHubClient.setOAuth2Token(configuration.getGitHubToken());
        return gitHubClient;
    }

}
