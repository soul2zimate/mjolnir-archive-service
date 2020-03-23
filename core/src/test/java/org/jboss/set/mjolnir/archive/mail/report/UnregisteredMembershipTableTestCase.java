package org.jboss.set.mjolnir.archive.mail.report;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.eclipse.egit.github.core.Team;
import org.jboss.set.mjolnir.archive.domain.RegisteredUser;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.set.mjolnir.archive.util.TestUtils.readSampleResponse;

@RunWith(CdiTestRunner.class)
public class UnregisteredMembershipTableTestCase {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Inject
    private EntityManager em;

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Inject
    private UnregisteredMembersReportTable unregisteredMembersReportTable;

    @Before
    public void setup() throws IOException, URISyntaxException {
        stubFor(get(urlPathEqualTo("/api/v3/orgs/testorg/teams"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readSampleResponse("responses/gh-orgs-teams-response.json"))));

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

        stubFor(get(urlPathEqualTo("/api/v3/teams/1/members/bob"))
                .willReturn(aResponse()
                        .withStatus(204)));

        stubFor(get(urlPathEqualTo("/api/v3/teams/2/members/bob"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readSampleResponse("responses/gh-orgs-members-not-found-response.json"))));

        stubFor(get(urlPathEqualTo("/api/v3/teams/3/members/bob"))
                .willReturn(aResponse()
                        .withStatus(204)));

        em.getTransaction().begin();

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setGithubName("ben");
        em.persist(registeredUser);

        em.getTransaction().commit();
    }

    @Test
    public void testComposeTableBody() throws IOException {
        final String testUser = "bob";

        List<Team> testUserTeams = ldapScanningBean.getAllUsersTeams(testUser);

        String messageBody = unregisteredMembersReportTable.composeTable();
        Document doc = Jsoup.parse(messageBody);

        assertThat(doc.select("tr").size()).isEqualTo(testUserTeams.size() + 2);
        assertThat(doc.select("th").text()).isEqualTo("Name Team");

        Elements elements = doc.select("td");
        assertThat(elements.size()).isEqualTo(testUserTeams.size() + 2);

        assertThat(elements.get(0).childNode(0)).isEqualTo(testUser);
        assertThat(elements.get(2).childNode(0)).isEqualTo(testUserTeams.get(0));
        assertThat(elements.get(3).childNode(0)).isEqualTo(testUserTeams.get(1));
    }
}
