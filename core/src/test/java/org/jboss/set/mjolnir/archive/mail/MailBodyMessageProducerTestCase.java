package org.jboss.set.mjolnir.archive.mail;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jboss.set.mjolnir.archive.domain.RegisteredUser;
import org.jboss.set.mjolnir.archive.domain.RemovalStatus;
import org.jboss.set.mjolnir.archive.domain.UserRemoval;
import org.jboss.set.mjolnir.archive.ldap.LdapDiscoveryBean;
import org.jboss.set.mjolnir.archive.ldap.LdapScanningBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.set.mjolnir.archive.util.TestUtils.createUserRemoval;
import static org.jboss.set.mjolnir.archive.util.TestUtils.readSampleResponse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(CdiTestRunner.class)
public class MailBodyMessageProducerTestCase {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Inject
    private EntityManager em;

    @Inject
    private RemovalsReportBean removalsReportBean;

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Inject
    private MailBodyMessageProducer mailBodyMessageProducer;

    @Before
    public void setup() throws IllegalAccessException, NoSuchFieldException, IOException, URISyntaxException {

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

        stubFor(get(urlPathEqualTo("/api/v3/teams/1/members/ben"))
                .willReturn(aResponse()
                        .withStatus(204)));

        stubFor(get(urlPathEqualTo("/api/v3/teams/2/members/ben"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readSampleResponse("responses/gh-orgs-members-not-found-response.json"))));

        stubFor(get(urlPathEqualTo("/api/v3/teams/3/members/ben"))
                .willReturn(aResponse()
                        .withStatus(204)));

        Timestamp now = new Timestamp(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, 1);

        Timestamp oneDayLater = new Timestamp(calendar.getTime().getTime());

        em.getTransaction().begin();

        UserRemoval userRemoval = createUserRemoval("User1", oneDayLater, oneDayLater, RemovalStatus.COMPLETED);
        em.persist(userRemoval);

        userRemoval = createUserRemoval("User2", oneDayLater, oneDayLater, RemovalStatus.FAILED);
        em.persist(userRemoval);

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
        registeredUser.setGithubName("bruno");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("joe");
        registeredUser.setWhitelisted(false);
        em.persist(registeredUser);

        em.getTransaction().commit();
    }

    @Test
    public void testComposeMessageBody() throws IOException, NamingException, NoSuchFieldException, IllegalAccessException {
        LdapDiscoveryBean ldapDiscoveryBean = mock(LdapDiscoveryBean.class);
        doReturn(false).when(ldapDiscoveryBean).checkUserExists("bobNonExisting");
        doReturn(true).when(ldapDiscoveryBean).checkUserExists("jimExisting");

        Field ldapDiscoveryBeanField = LdapScanningBean.class.getDeclaredField("ldapDiscoveryBean");
        ldapDiscoveryBeanField.setAccessible(true);
        ldapDiscoveryBeanField.set(ldapScanningBean, ldapDiscoveryBean);

        Field ldapScanningBeanField = MailBodyMessageProducer.class.getDeclaredField("ldapScanningBean");
        ldapScanningBeanField.setAccessible(true);
        ldapScanningBeanField.set(mailBodyMessageProducer, ldapScanningBean);

        List<UserRemoval> lastFinishedRemovals = removalsReportBean.getLastFinishedRemovals();

        String messageBody = mailBodyMessageProducer.composeMessageBody();
        Document doc = Jsoup.parse(messageBody);

        /*
        assertThat(doc.select("tr").size()).isEqualTo(lastFinishedRemovals.size() + 1);
        assertThat(doc.select("th").text()).isEqualTo("Name Created Started Status");

        Elements elements = doc.select("td");
        assertThat(elements.size()).isEqualTo(lastFinishedRemovals.size() * 4);

        for (int i = 0; i < lastFinishedRemovals.size(); i++) {
            assertThat(elements.get((i * 4)).childNode(0).toString()).isEqualTo(lastFinishedRemovals.get(i).getUsername());
            assertThat(elements.get((i * 4) + 1).childNode(0).toString()).isEqualTo(lastFinishedRemovals.get(i).getCreated().toString());
            assertThat(elements.get((i * 4) + 2).childNode(0).toString()).isEqualTo(lastFinishedRemovals.get(i).getStarted().toString());
            assertThat(elements.get((i * 4) + 3).childNode(0).toString()).isEqualTo(lastFinishedRemovals.get(i).getStatus().toString());
        }
         */
    }
}
