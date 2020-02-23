package org.jboss.set.mjolnir.archive.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_removals")
public class UserRemoval {

    @Id
    private Long id;

    private String username;

    /**
     * When should the membership be removed?
     */
    private LocalDate removeOn;

    @CreationTimestamp
    private LocalDateTime created;

    /**
     * Date when the removal process started.
     */
    private LocalDateTime started;

    /**
     * Date when the removal process completed.
     */
    private LocalDateTime completed;

    @Enumerated(EnumType.STRING)
    private RemovalStatus status;

    @OneToMany
    private List<RepositoryFork> forks;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getRemoveOn() {
        return removeOn;
    }

    public void setRemoveOn(LocalDate removeOn) {
        this.removeOn = removeOn;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getCompleted() {
        return completed;
    }

    public void setCompleted(LocalDateTime completed) {
        this.completed = completed;
    }

    public RemovalStatus getStatus() {
        return status;
    }

    public void setStatus(RemovalStatus status) {
        this.status = status;
    }

    public List<RepositoryFork> getForks() {
        return forks;
    }

    public void setForks(List<RepositoryFork> forks) {
        this.forks = forks;
    }
}
