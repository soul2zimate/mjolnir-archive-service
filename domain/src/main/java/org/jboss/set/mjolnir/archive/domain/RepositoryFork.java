package org.jboss.set.mjolnir.archive.domain;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class RepositoryFork {

    private String repositoryName;

    private String repositoryUrl;

    private String sourceRepositoryName;

    private String sourceRepositoryUrl;

    @CreationTimestamp
    private LocalDateTime created;

    private LocalDateTime archived;

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getSourceRepositoryName() {
        return sourceRepositoryName;
    }

    public void setSourceRepositoryName(String sourceRepositoryName) {
        this.sourceRepositoryName = sourceRepositoryName;
    }

    public String getSourceRepositoryUrl() {
        return sourceRepositoryUrl;
    }

    public void setSourceRepositoryUrl(String sourceRepositoryUrl) {
        this.sourceRepositoryUrl = sourceRepositoryUrl;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getArchived() {
        return archived;
    }

    public void setArchived(LocalDateTime archived) {
        this.archived = archived;
    }
}
