package org.jboss.set.mjolnir.archive;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.TeamService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.set.mjolnir.archive.util.TestUtils.readSampleResponse;
import static org.junit.Assert.*;

public class GitHubUserRemovalBeanTestCase {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private GitHubClient client = new GitHubClient("localhost", 8089, "http");

    @Before
    public void setup() throws IOException, URISyntaxException {
        stubFor(get(urlPathEqualTo("/api/v3/orgs/testorg/teams"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readSampleResponse("responses/gh-orgs-teams-response.json"))));

        stubFor(get(urlPathEqualTo("/api/v3/teams/1/members/lvydra"))
                .willReturn(aResponse()
                        .withStatus(204)));

        stubFor(get(urlPathEqualTo("/api/v3/teams/2/members/lvydra"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readSampleResponse("responses/gh-orgs-members-not-found-response.json"))));

        stubFor(get(urlPathEqualTo("/api/v3/teams/3/members/lvydra"))
                .willReturn(aResponse()
                        .withStatus(204)));

        stubFor(delete(urlPathEqualTo("/api/v3/teams/1/members/lvydra"))
                .willReturn(aResponse()
                        .withStatus(204)));

        stubFor(delete(urlPathEqualTo("/api/v3/teams/3/members/lvydra"))
                .willReturn(aResponse()
                        .withStatus(204)));
    }

    @Test
    public void testWiremockResponses() throws Exception {
        TeamService teamService = new TeamService(client);

        List<Team> organizationTeams = teamService.getTeams("testorg");
        assertEquals(3, organizationTeams.size());
        assertThat(organizationTeams)
                .extracting("name")
                .containsOnly("Team 1", "Team 2", "Team 3");

        assertTrue(teamService.isMember(1, "lvydra"));
        assertFalse(teamService.isMember(2, "lvydra"));
        assertTrue(teamService.isMember(3, "lvydra"));
    }

    @Test
    public void testRemoveUsersFromTeam() throws Exception {
        GitHubUserRemovalBean bean = new GitHubUserRemovalBean(client);

        bean.removeUserFromTeams("testorg", "lvydra");

        verify(deleteRequestedFor(urlEqualTo("/api/v3/teams/1/members/lvydra")));
        verify(exactly(0), deleteRequestedFor(urlEqualTo("/api/v3/teams/2/members/lvydra")));
        verify(deleteRequestedFor(urlEqualTo("/api/v3/teams/3/members/lvydra")));
    }

}
