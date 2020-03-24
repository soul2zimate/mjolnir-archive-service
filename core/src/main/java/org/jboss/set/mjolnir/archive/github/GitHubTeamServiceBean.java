package org.jboss.set.mjolnir.archive.github;

import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides user membership management capabilities.
 */
public class GitHubTeamServiceBean {

    private Logger logger = Logger.getLogger(getClass());

    private CustomizedTeamService teamService;

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
    public void removeUserFromTeams(String organisation, String githubUser) throws IOException {
        logger.infof("Removing %s users team memberships", githubUser);

        List<Team> organizationTeams = getTeams(organisation);

        for (Team team : organizationTeams) {
            if (isMember(githubUser, team)) {
                logger.infof("Removing %s users %s team membership", githubUser, team.getName());
                teamService.removeMember(team.getId(), githubUser);
            }
        }
    }

    /**
     * Retrieves members of all organization teams.
     */
    public Set<User> getAllTeamsMembers(String organization) throws IOException {
        List<Team> teams = teamService.getTeams(organization);
        Set<User> members = new HashSet<>();
        for (Team team : teams) {
            members.addAll(teamService.getMembers(organization, team.getId()));
        }
        return members;
    }

    public List<Team> getTeams(String organisation) throws IOException {
        return teamService.getTeams(organisation);
    }

    public boolean isMember(String githubUser, Team team) throws IOException {
        return teamService.isMember(team.getId(), githubUser);
    }
}
