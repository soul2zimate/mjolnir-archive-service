package org.jboss.set.mjolnir.archive;

import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.TeamService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Provides user memberships removal.
 */
public class GitHubUserRemovalBean {

    private Logger logger = Logger.getLogger(getClass());

    private TeamService teamService;

    @Inject
    public GitHubUserRemovalBean(GitHubClient client) {
        teamService = new TeamService(client);
    }

    /**
     * Find teams for given organization filter ones in which user got membership and then removes users membership.
     *
     * @param organisation github organization
     * @param githubUser   github username
     */
    public void removeUserFromTeams(String organisation, String githubUser) throws IOException {
        logger.infof("Removing %s users team memberships", githubUser);

        List<Team> organizationTeams = teamService.getTeams(organisation);

        organizationTeams.stream()
                .filter(team -> isUserTeamMember(team.getId(), githubUser))
                .forEach(team -> removeMemberFromTeam(team, githubUser));
    }

    private boolean isUserTeamMember(int teamId, String githubUser) {
        try {
            return teamService.isMember(teamId, githubUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void removeMemberFromTeam(Team team, String githubUser) {
        logger.infof("Removing %s users %s team membership", githubUser, team.getName());

        try {
            teamService.removeMember(team.getId(), githubUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
