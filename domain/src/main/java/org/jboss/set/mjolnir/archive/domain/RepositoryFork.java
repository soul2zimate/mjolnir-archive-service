package org.jboss.set.mjolnir.archive.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Stores information about discovered repository forks of removed user.
 */
@Entity
@Table(name = "repository_fork")
public class RepositoryFork {

    @Id
    private Long id;

    private String repositoryName;

    private String repositoryUrl;

    private String sourceRepositoryName;

    private String sourceRepositoryUrl;

    @CreationTimestamp
    private Timestamp created;

    private Timestamp archived;

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

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getArchived() {
        return archived;
    }

    public void setArchived(Timestamp archived) {
        this.archived = archived;
    }
}
