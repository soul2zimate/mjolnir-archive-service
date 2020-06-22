package org.jboss.set.mjolnir.archive.github;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.domain.GitHubOrganization;
import org.jboss.set.mjolnir.archive.domain.GitHubTeam;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides user membership management capabilities.
 */
public class GitHubTeamServiceBean {

    private final Logger logger = Logger.getLogger(getClass());

    private final CustomizedTeamService teamService;

    @Inject
    public GitHubTeamServiceBean(GitHubClient client) {
        teamService = new CustomizedTeamService(client);
    }

    /**
     * Find teams for given organization filter ones in which user got membership and then removes users membership.
     *
     * @param organisation github organization
     * @param githubUser   github username
     */
    public void removeUserFromTeams(GitHubOrganization organisation, String githubUser) throws IOException {
        logger.infof("Removing team memberships of user %s", githubUser);

        List<GitHubTeam> teams = organisation.getTeams();

        for (GitHubTeam team : teams) {
            if (isMember(githubUser, team)) {
                logger.infof("Removing membership of user %s in team %s", githubUser, team.getName());
                removeMembership(githubUser, team);
            } else {
                logger.infof("User %s is not a member of team %s", githubUser, team.getName());
            }
        }
    }

    /**
     * Retrieves members of all organization teams.
     */
    public Set<User> getAllTeamsMembers(GitHubOrganization organization) throws IOException {
        Set<User> members = new HashSet<>();
        for (GitHubTeam team : organization.getTeams()) {
            members.addAll(teamService.getMembers(organization.getName(), team.getGithubId()));
        }
        return members;
    }

    public boolean isMember(String githubUser, GitHubTeam team) throws IOException {
        return teamService.isMember(team.getGithubId(), githubUser);
    }

    public void removeMembership(String githubUser, GitHubTeam team) throws IOException {
        teamService.removeMember(team.getGithubId(), githubUser);
    }
}
