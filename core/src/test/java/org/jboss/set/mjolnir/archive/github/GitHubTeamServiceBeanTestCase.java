package org.jboss.set.mjolnir.archive.github;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.set.mjolnir.archive.util.TestUtils.readSampleResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GitHubTeamServiceBeanTestCase {

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

        stubFor(get(urlPathEqualTo("/api/v3/orgs/testorg/team/1/members"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readSampleResponse("responses/gh-orgs-team-1-members-response.json"))));

        stubFor(get(urlPathEqualTo("/api/v3/orgs/testorg/team/2/members"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readSampleResponse("responses/gh-orgs-team-2-members-response.json"))));

        stubFor(get(urlPathEqualTo("/api/v3/orgs/testorg/team/3/members"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readSampleResponse("responses/empty-list-response.json"))));
    }

    @Test
    public void testWiremockResponses() throws Exception {
        CustomizedTeamService teamService = new CustomizedTeamService(client);

        List<Team> organizationTeams = teamService.getTeams("testorg");
        assertEquals(3, organizationTeams.size());
        assertThat(organizationTeams)
                .extracting("name")
                .containsOnly("Team 1", "Team 2", "Team 3");

        assertTrue(teamService.isMember(1, "lvydra"));
        assertFalse(teamService.isMember(2, "lvydra"));
        assertTrue(teamService.isMember(3, "lvydra"));

        assertThat(teamService.getMembers("testorg", 1))
                .extracting("login")
                .containsOnly("bob");
        assertThat(teamService.getMembers("testorg", 2))
                .extracting("login")
                .containsOnly("ben");
        assertThat(teamService.getMembers("testorg", 3))
                .isEmpty();

    }

    @Test
    public void testRemoveUsersFromTeam() throws Exception {
        GitHubTeamServiceBean bean = new GitHubTeamServiceBean(client);

        bean.removeUserFromTeams("testorg", "lvydra");

        verify(deleteRequestedFor(urlEqualTo("/api/v3/teams/1/members/lvydra")));
        verify(exactly(0), deleteRequestedFor(urlEqualTo("/api/v3/teams/2/members/lvydra")));
        verify(deleteRequestedFor(urlEqualTo("/api/v3/teams/3/members/lvydra")));
    }

    @Test
    public void testGetAllTeamsMembers() throws Exception {
        GitHubTeamServiceBean bean = new GitHubTeamServiceBean(client);

        Set<User> members = bean.getAllTeamsMembers("testorg");
        assertThat(members).extracting("login")
                .containsOnly("bob", "ben");
    }
}
