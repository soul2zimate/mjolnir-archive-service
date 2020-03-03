package org.jboss.set.mjolnir.archive.configuration;

import javax.enterprise.inject.Vetoed;
import java.net.URI;

@Vetoed
public class Configuration {

    private String gitHubToken;
    private String gitHubApiHost;
    private Integer gitHubApiPort;
    private String gitHubApiScheme;
    private String repositoryArchiveRoot;

    public Configuration() {
    }

    public String getGitHubToken() {
        return gitHubToken;
    }

    public String getGitHubApiHost() {
        return gitHubApiHost;
    }

    public Integer getGitHubApiPort() {
        return gitHubApiPort;
    }

    public String getGitHubApiScheme() {
        return gitHubApiScheme;
    }

    public String getRepositoryArchiveRoot() {
        return repositoryArchiveRoot;
    }

    public static class ConfigurationBuilder {

        private Configuration configuration = new Configuration();

        public ConfigurationBuilder setGitHubToken(String gitHubToken) {
            this.configuration.gitHubToken = gitHubToken;
            return this;
        }

        public ConfigurationBuilder setGitHubApiUri(URI uri) {
            this.configuration.gitHubApiHost = uri.getHost();
            this.configuration.gitHubApiPort = uri.getPort();
            this.configuration.gitHubApiScheme = uri.getScheme();
            return this;
        }

        public ConfigurationBuilder setRepositoryArchiveRoot(String repositoryArchiveRoot) {
            this.configuration.repositoryArchiveRoot = repositoryArchiveRoot;
            return this;
        }

        public Configuration build() {
            return configuration;
        }
    }
}
