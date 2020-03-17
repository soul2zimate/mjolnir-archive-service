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
    private String reportingEmail;
    private String ldapUrl;
    private String ldapSearchContext;
    private boolean unsubscribeUsers = false;

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

    /**
     * Path to the archive directory where user repositories are to be archived.
     */
    public String getRepositoryArchiveRoot() {
        return repositoryArchiveRoot;
    }

    /**
     * Email address where reporting emails will be sent to.
     */
    public String getReportingEmail() {
        return reportingEmail;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public String getLdapSearchContext() {
        return ldapSearchContext;
    }

    /**
     * If true, processed users will be really unsubscribed from the GitHub teams. If false, users will be just
     * reported (user repositories will still be archived).
     */
    public boolean isUnsubscribeUsers() {
        return unsubscribeUsers;
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

        public void setReportingEmail(String reportingEmail) {
            this.configuration.reportingEmail = reportingEmail;
        }

        public void setUnsubscribeUsers(boolean unsubscribeUsers) {
            this.configuration.unsubscribeUsers = unsubscribeUsers;
        }

        public void setLdapUrl(String url) {
            this.configuration.ldapUrl = url;
        }

        public void setLdapSearchContext(String context) {
            this.configuration.ldapSearchContext = context;
        }

        public Configuration build() {
            return configuration;
        }
    }
}
