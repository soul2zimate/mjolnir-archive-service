package org.jboss.set.mjolnir.archive.mail.report;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jboss.set.mjolnir.archive.domain.RegisteredUser;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(CdiTestRunner.class)
public class WhitelistedUsersWithoutLdapReportTableTestCase {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Inject
    private EntityManager em;

    @Inject
    private LdapScanningBean ldapScanningBean;

    @Inject
    private WhitelistedUsersWithoutLdapReportTable whitelistedUsersWithoutLdapReportTable;

    @Before
    public void setup() {
        em.getTransaction().begin();

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setGithubName("bob");
        registeredUser.setKerberosName("bobNonExisting");
        registeredUser.setResponsiblePerson("Responsible guy");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("jim");
        registeredUser.setKerberosName("jimExisting");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("carl");
        registeredUser.setKerberosName("carlNonExisting");
        registeredUser.setResponsiblePerson("Responsible guy");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("sam");
        registeredUser.setKerberosName("samNonExisting");
        registeredUser.setResponsiblePerson("Responsible guy");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("bruno");
        registeredUser.setResponsiblePerson("Responsible guy");
        registeredUser.setWhitelisted(true);
        em.persist(registeredUser);

        registeredUser = new RegisteredUser();
        registeredUser.setGithubName("joe");
        registeredUser.setWhitelisted(false);
        em.persist(registeredUser);

        em.getTransaction().commit();
    }

    @Test
    public void testComposeTableBody() throws NamingException, NoSuchFieldException, IllegalAccessException {
        LdapDiscoveryBean ldapDiscoveryBean = mock(LdapDiscoveryBean.class);
        doReturn(false).when(ldapDiscoveryBean).checkUserExists("bobNonExisting");
        doReturn(false).when(ldapDiscoveryBean).checkUserExists("carlNonExisting");
        doReturn(false).when(ldapDiscoveryBean).checkUserExists("samNonExisting");
        doReturn(true).when(ldapDiscoveryBean).checkUserExists("jimExisting");

        Field ldapDiscoveryBeanField = LdapScanningBean.class.getDeclaredField("ldapDiscoveryBean");
        ldapDiscoveryBeanField.setAccessible(true);
        ldapDiscoveryBeanField.set(ldapScanningBean, ldapDiscoveryBean);

        Field ldapScanningBeanField = WhitelistedUserReportTable.class.getDeclaredField("ldapScanningBean");
        ldapScanningBeanField.setAccessible(true);
        ldapScanningBeanField.set(whitelistedUsersWithoutLdapReportTable, ldapScanningBean);

        Set<RegisteredUser> users = ldapScanningBean.getWhitelistedUsersWithoutLdapAccount();
        List<RegisteredUser> usersList = new ArrayList<>(users);
        usersList.sort((user1, user2) -> user1.getGithubName().compareToIgnoreCase(user2.getGithubName()));

        String messageBody = whitelistedUsersWithoutLdapReportTable.composeTable();
        Document doc = Jsoup.parse(messageBody);

        assertThat(doc.select("tr").size()).isEqualTo(users.size() + 1);
        assertThat(doc.select("th").text()).isEqualTo("Name Responsible person");

        Elements elements = doc.select("td");
        assertThat(elements.size()).isEqualTo(users.size() * 2);

        int i = 0;
        for (RegisteredUser user : usersList) {
            assertThat(user.getGithubName()).isEqualTo(elements.get(i).childNode(0).toString());
            assertThat(user.getResponsiblePerson()).isEqualTo(elements.get(i + 1).childNode(0).toString());
            i += 2;
        }
    }
}
