package org.jboss.set.mjolnir.archive.configuration;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class Configuration {

    private String githubToken;

    public Configuration() {
    }

    public String getGithubToken() {
        return githubToken;
    }

    void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }
}
