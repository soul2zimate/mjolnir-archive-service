package org.jboss.set.mjolnir.archive.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * Represents request for user removal and stores information about the removal's progress and result.
 */
@NamedQueries({
        @NamedQuery(name = UserRemoval.FIND_REMOVALS_TO_PROCESS,
                query = "SELECT r FROM UserRemoval r WHERE r.started IS NULL" +
                        " AND (remove_on <= CURRENT_DATE or remove_on IS NULL)"),
        @NamedQuery(name = UserRemoval.FIND_FINISHED_REMOVALS,
                query = "SELECT r FROM UserRemoval r WHERE r.completed > :jobStart AND r.status != 'STARTED' ORDER BY r.status DESC, r.created")
})
@Entity
@Table(name = "user_removals")
public class UserRemoval {

    public static final String FIND_REMOVALS_TO_PROCESS = "UserRemoval.findRemovalsToProcess";
    public static final String FIND_FINISHED_REMOVALS = "UserRemoval.findFinishedRemovals";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_removals_generator")
    @SequenceGenerator(name="user_removals_generator", sequenceName = "sq_user_removals", allocationSize = 1)
    @SuppressWarnings("unused")
    private Long id;

    @Column(name = "ldap_username")
    private String ldapUsername;

    @Column(name = "github_username")
    private String githubUsername;

    /**
     * When should the membership be removed?
     */
    @Column(name = "remove_on")
    private Date removeOn;

    @CreationTimestamp
    private Timestamp created;

    /**
     * Date when the removal process started.
     */
    private Timestamp started;

    /**
     * Date when the removal process completed.
     */
    private Timestamp completed;

    @Enumerated(EnumType.STRING)
    private RemovalStatus status;

    @OneToMany
    @JoinColumn(name = "user_removal_id")
    private List<RepositoryFork> forks;

    @OneToMany
    @JoinColumn(name = "user_removal_id")
    private List<RemovalLog> logs;

    public Long getId() {
        return id;
    }

    public Date getRemoveOn() {
        return removeOn;
    }

    public void setRemoveOn(Date removeOn) {
        this.removeOn = removeOn;
    }

    public Timestamp getStarted() {
        return started;
    }

    public void setStarted(Timestamp started) {
        this.started = started;
    }

    public String getLdapUsername() {
        return ldapUsername;
    }

    public void setLdapUsername(String ldapUsername) {
        this.ldapUsername = ldapUsername;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public Timestamp getCreated() {
        return created;
    }

    public Timestamp getCompleted() {
        return completed;
    }

    public RemovalStatus getStatus() {
        return status;
    }

    public void setStatus(RemovalStatus status) {
        this.status = status;
        this.completed = new Timestamp(System.currentTimeMillis());
    }

    public List<RepositoryFork> getForks() {
        return forks;
    }

    public void setForks(List<RepositoryFork> forks) {
        this.forks = forks;
    }

    public List<RemovalLog> getLogs() {
        return logs;
    }

    public void setLogs(List<RemovalLog> logs) {
        this.logs = logs;
    }

    @Override
    public String toString() {
        return "UserRemoval{" +
                "id=" + id +
                ", ldapUsername='" + ldapUsername + '\'' +
                ", githubUsername='" + githubUsername + '\'' +
                ", removeOn=" + removeOn +
                ", created=" + created +
                ", started=" + started +
                ", completed=" + completed +
                ", status=" + status +
                '}';
    }
}
