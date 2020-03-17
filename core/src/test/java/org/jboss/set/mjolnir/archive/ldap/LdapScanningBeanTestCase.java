package org.jboss.set.mjolnir.archive.ldap;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.set.mjolnir.archive.util.TestUtils.readSampleResponse;

@RunWith(CdiTestRunner.class)
public class LdapScanningBeanTestCase {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Inject
    private EntityManager em;

    @Inject
    private LdapScanningBean usersDetection;

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
    }

    @Test
    public void testCreateUserRemovals() {
        TypedQuery<UserRemoval> query = em.createNamedQuery(UserRemoval.FIND_REMOVALS_TO_PROCESS, UserRemoval.class);
        Assert.assertTrue(query.getResultList().isEmpty());

        usersDetection.createUserRemovals(Arrays.asList("ben", "bob"));

        List<UserRemoval> removals = query.getResultList();
        assertThat(removals)
                .extracting("username")
                .containsOnly("ben", "bob");
    }

    @Test
    public void testAllOrganizationMembers() throws IOException {
        Set<String> members = usersDetection.getAllOrganizationsMembers();
        assertThat(members).containsOnly("bob", "ben");
    }

    @Test
    public void testAllOrganization() {
        usersDetection.createRemovalsForUsersWithoutLdapAccount();

        TypedQuery<UserRemoval> query = em.createNamedQuery(UserRemoval.FIND_REMOVALS_TO_PROCESS, UserRemoval.class);
        List<UserRemoval> removals = query.getResultList();
        assertThat(removals)
                .extracting("username")
                .containsOnly("ben", "bob");
    }
}
