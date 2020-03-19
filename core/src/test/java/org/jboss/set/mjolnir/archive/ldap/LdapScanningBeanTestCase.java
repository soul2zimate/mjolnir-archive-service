package org.jboss.set.mjolnir.archive.ldap;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.eclipse.egit.github.core.Team;
import org.jboss.set.mjolnir.archive.domain.RegisteredUser;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.lang.reflect.Field;
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
import static org.mockito.Mockito.*;

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
    public void testUnregisteredOrganizationMembers() throws IOException {
        em.getTransaction().begin();

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setGithubName("bob");
        em.persist(registeredUser);

        List<String> members = usersDetection.getUnregisteredOrganizationMembers();
        assertThat(members).containsOnly("ben");

        em.getTransaction().rollback();
    }

    @Test
    public void testWhitelistedUsersWithoutLdapAccount() throws IOException, NamingException, NoSuchFieldException, IllegalAccessException {
        LdapDiscoveryBean ldapDiscoveryBean = mock(LdapDiscoveryBean.class);
        doReturn(false).when(ldapDiscoveryBean).checkUserExists("bobNonExisting");    // Mock implementation
        doReturn(true).when(ldapDiscoveryBean).checkUserExists("jimExisting");

        Field ldapDiscoveryBeanField = LdapScanningBean.class.getDeclaredField("ldapDiscoveryBean");
        ldapDiscoveryBeanField.setAccessible(true);

        ldapDiscoveryBeanField.set(usersDetection, ldapDiscoveryBean);

        em.getTransaction().begin();

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setGithubName("bob");
        registeredUser.setKerberosName("bobNonExisting");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("jim");
        registeredUser.setKerberosName("jimExisting");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("ben");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("joe");
        registeredUser.setWhitelisted(false);
        em.persist(registeredUser);

        Set<String> members = usersDetection.getWhitelistedUsersWithoutLdapAccount();
        assertThat(members).containsOnly("bob", "ben");

        em.getTransaction().rollback();
    }

    @Test
    public void testAllUsersTeams() throws IOException {
        List<Team> teams = usersDetection.getAllUsersTeams("bob");
        assertThat(teams)
                .extracting("name")
                .containsOnly("Team 1", "Team 3");
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
