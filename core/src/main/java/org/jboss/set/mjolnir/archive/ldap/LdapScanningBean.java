package org.jboss.set.mjolnir.archive.ldap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.github.GitHubTeamServiceBean;
import org.jboss.set.mjolnir.archive.domain.GitHubOrganization;
import org.jboss.set.mjolnir.archive.domain.RegisteredUser;
import org.jboss.set.mjolnir.archive.domain.RemovalLog;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.jboss.set.mjolnir.archive.domain.repositories.RegisteredUserRepositoryBean;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Discovers users that left the company and creates their removal records.
 *
 * This implementation works by querying LDAP database. It should eventually be replaced by an implementation
 * relying on JMS messages.
 */
public class LdapScanningBean {

    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @Inject
    private LdapDiscoveryBean ldapDiscoveryBean;

    @Inject
    private GitHubTeamServiceBean gitHubBean;

    @Inject
    private RegisteredUserRepositoryBean userRepositoryBean;


    public void createRemovalsForUsersWithoutLdapAccount() {
        try {
            doCreateRemovalsForUsersWithoutLdapAccount();
        } catch (IOException | NamingException e) {
            logger.error("Failed to create user removals", e);
            RemovalLog log = new RemovalLog();
            log.setStackTrace(e);
            log.setMessage("Failed to create user removals");

            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.persist(log);
            transaction.commit();
        }
    }

    void doCreateRemovalsForUsersWithoutLdapAccount() throws IOException, NamingException {
        logger.infof("Starting job to create user removals");

        // collect members of all teams
        Set<String> allMembers = getAllOrganizationsMembers();
        logger.infof("Found %d members of all organizations teams.", allMembers.size());

        // retrieve kerberos names of collected users (those that we know and are not whitelisted)
        HashSet<String> krbNames = new HashSet<>();
        allMembers.forEach(member -> {
            Optional<RegisteredUser> registeredUser = userRepositoryBean.findByGitHubUsername(member);
            registeredUser.ifPresent(user -> {
                if (user.isWhitelisted()) {
                    logger.infof("Skipping whitelisted user %s.", user.getGithubName());
                } else if (StringUtils.isBlank(user.getKerberosName())) {
                    logger.warnf("Skipping user %s because of unknown LDAP name.", user.getGithubName());
                } else {
                    krbNames.add(user.getKerberosName());
                }
            });
        });
        logger.infof("Out of all members, %d are registered users.", krbNames.size());

        // search for users that do not have active LDAP account
        Map<String, Boolean> usersLdapMap = ldapDiscoveryBean.checkUsersExists(krbNames);
        Set<String> usersWithoutLdapAccount = usersLdapMap.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        logger.infof("Detected %d users that do not have active LDAP account.", usersWithoutLdapAccount.size());

        // create removal records
        createUserRemovals(usersWithoutLdapAccount);
    }

    public Set<String> getWhitelistedUsersWithoutLdapAccount() throws NamingException {
        List<RegisteredUser> whitelistedUsers = em.createNamedQuery(RegisteredUser.FIND_WHITELISTED, RegisteredUser.class).getResultList();

        Set<String> whitelistedUsersWithoutLdapAccount = new HashSet<>();
        for (RegisteredUser whitelistedUser : whitelistedUsers) {
            if (StringUtils.isBlank(whitelistedUser.getKerberosName()) || !ldapDiscoveryBean.checkUserExists(whitelistedUser.getKerberosName()))
                whitelistedUsersWithoutLdapAccount.add(whitelistedUser.getGithubName());
        }

        return whitelistedUsersWithoutLdapAccount;
    }

    /**
     * Collects members of all teams of all registered GitHub organizations.
     */
    public Set<String> getAllOrganizationsMembers() throws IOException {
        List<GitHubOrganization> organizations =
                em.createNamedQuery(GitHubOrganization.FIND_ALL, GitHubOrganization.class).getResultList();

        HashSet<User> users = new HashSet<>();
        for (GitHubOrganization organization : organizations) {
            users.addAll(gitHubBean.getAllTeamsMembers(organization.getName()));
        }

        return users.stream()
                .map(User::getLogin)
                .collect(Collectors.toSet());
    }

    public List<String> getUnregisteredOrganizationMembers() throws IOException {
        Set<String> allMembers = getAllOrganizationsMembers();
        List<RegisteredUser> registeredUsers = em.createNamedQuery(RegisteredUser.FIND_ALL, RegisteredUser.class).getResultList();

        List<String> unregisteredMembers = allMembers.stream()
                .filter(user -> !containsRegisteredUser(user, registeredUsers))
                .collect(Collectors.toList());

        return unregisteredMembers;
    }

    public List<Team> getAllUsersTeams(String gitHubUser) throws IOException {
        List<Team> memberTeams = new ArrayList<>();

        List<GitHubOrganization> organizations =
                em.createNamedQuery(GitHubOrganization.FIND_ALL, GitHubOrganization.class).getResultList();

        List<Team> allTeams = new ArrayList<>();
        for (GitHubOrganization organization : organizations) {
            allTeams.addAll(gitHubBean.getTeams(organization.getName()));
        }

        for (Team team : allTeams) {
            if (gitHubBean.isMember(gitHubUser, team))
                memberTeams.add(team);
        }

        return memberTeams;
    }

    private boolean containsRegisteredUser(String member, List<RegisteredUser> registeredUsers) {
        boolean isMember = false;

        for (RegisteredUser registeredUser : registeredUsers) {
            isMember = member.equals(registeredUser.getGithubName());

            if (isMember)
                break;
        }

        return isMember;
    }

    /**
     * Creates and perists UserRemoval objects for given list of usernames.
     */
    void createUserRemovals(Collection<String> krbNames) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        krbNames.forEach(username -> {
            logger.infof("Creating removal record for user %s", username);
            UserRemoval removal = new UserRemoval();
            removal.setUsername(username);
            em.persist(removal);
        });

        transaction.commit();
    }
}
