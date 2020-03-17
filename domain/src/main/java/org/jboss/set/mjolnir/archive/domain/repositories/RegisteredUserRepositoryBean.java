package org.jboss.set.mjolnir.archive.domain.repositories;

import org.jboss.set.mjolnir.archive.domain.RegisteredUser;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.Optional;

import static org.jboss.set.mjolnir.archive.domain.repositories.JpaUtils.findSingleResult;

/**
 * User repository - methods for obtaining persisted users.
 */
public class RegisteredUserRepositoryBean {

    @Inject
    private EntityManager em;

    public Optional<RegisteredUser> findByKrbUsername(String username) {
        TypedQuery<RegisteredUser> query = em.createNamedQuery(RegisteredUser.FIND_BY_KRB_NAME, RegisteredUser.class);
        query.setParameter("krbName", username);
        return findSingleResult(query);
    }

    public Optional<RegisteredUser> findByGitHubUsername(String username) {
        TypedQuery<RegisteredUser> query = em.createNamedQuery(RegisteredUser.FIND_BY_GITHUB_NAME, RegisteredUser.class);
        query.setParameter("githubName", username);
        return findSingleResult(query);
    }
}
